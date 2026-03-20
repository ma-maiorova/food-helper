package org.example.service

import org.example.api.dto.ImportErrorItemDto
import org.example.api.dto.ImportResultDto
import org.example.api.dto.ParserImportItemDto
import org.example.api.dto.ParserImportRequest
import org.example.api.errors.ValidationException
import org.example.config.AppMetrics
import org.example.repository.DeliveryServiceRepository
import org.example.repository.ProductImportRepository
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.TimeUnit

class ProductImportService(
    private val deliveryServiceRepository: DeliveryServiceRepository,
    private val productImportRepository: ProductImportRepository,
    private val maxItemsPerRequest: Int,
    private val chunkSize: Int
) {
    private val log = LoggerFactory.getLogger(ProductImportService::class.java)

    /**
     * Импортирует батч продуктов от парсера.
     *
     * Request-level checks (→ 400 whole request fails):
     * - items.size must be <= [maxItemsPerRequest]; exceeding is rejected entirely, not truncated
     * - items must not be empty
     * - deliveryServiceCode must not be blank
     *
     * Item-level checks (→ failed item, rest continue):
     * - name not blank, url not blank, url valid absolute http/https URL
     * - price >= 0, currency not blank
     * - weight > 0 if provided
     * - variant nutrient values >= 0 if provided
     *
     * Elements are chunked by [chunkSize]; each item runs in its own transaction.
     */
    suspend fun importBatch(request: ParserImportRequest): ImportResultDto {
        // Request-level: oversized → 400
        if (request.items.size > maxItemsPerRequest) {
            throw ValidationException(
                "Too many items: ${request.items.size}. Maximum allowed: $maxItemsPerRequest"
            )
        }

        // Request-level: empty items → 400
        if (request.items.isEmpty()) {
            throw ValidationException("items cannot be empty")
        }

        // Request-level: blank code → 400
        val code = request.deliveryServiceCode.trim()
        if (code.isEmpty()) {
            throw ValidationException("deliveryServiceCode cannot be blank")
        }

        AppMetrics.importRequestsTotal.increment()
        AppMetrics.importItemsTotal.increment(request.items.size.toDouble())

        log.info(
            "import.start deliveryServiceCode={} itemsCount={}",
            code, request.items.size
        )

        val startNs = System.nanoTime()
        val deliveryService = deliveryServiceRepository.findByCode(code)
            ?: return ImportResultDto(
                importedCount = 0,
                failedCount = request.items.size,
                errors = listOf(
                    ImportErrorItemDto(itemIndex = -1, message = "Служба доставки с кодом '$code' не найдена")
                )
            )

        val errors = mutableListOf<ImportErrorItemDto>()
        var importedCount = 0

        val chunks = request.items.chunked(chunkSize.coerceAtLeast(1))
        chunks.forEachIndexed { chunkIndex, chunk ->
            chunk.forEachIndexed { indexInChunk, item ->
                val globalIndex = chunkIndex * chunkSize + indexInChunk
                val validationError = validateItem(item, globalIndex)
                if (validationError != null) {
                    errors.add(validationError)
                    return@forEachIndexed
                }
                try {
                    val trimmedName = item.name.trim()
                    val trimmedUrl = item.url.trim()
                    val trimmedCurrency = item.currency.trim().take(8)

                    val productId = productImportRepository.upsertProduct(
                        deliveryServiceId = deliveryService.id,
                        name = trimmedName,
                        url = trimmedUrl,
                        price = item.price,
                        currency = trimmedCurrency
                    )
                    productImportRepository.replaceVariants(productId, item.variants)
                    importedCount++
                } catch (e: Exception) {
                    log.warn(
                        "import.item_error index={} url={} error={}",
                        globalIndex, item.url.take(256), e.message
                    )
                    errors.add(
                        ImportErrorItemDto(
                            itemIndex = globalIndex,
                            url = item.url.take(256),
                            name = item.name.take(256),
                            message = e.message ?: e.javaClass.simpleName
                        )
                    )
                }
            }
        }

        AppMetrics.importFailedItemsTotal.increment(errors.size.toDouble())
        AppMetrics.importDuration.record(System.nanoTime() - startNs, TimeUnit.NANOSECONDS)

        log.info(
            "import.finish deliveryServiceCode={} importedCount={} failedCount={}",
            code, importedCount, errors.size
        )

        return ImportResultDto(
            importedCount = importedCount,
            failedCount = errors.size,
            errors = errors
        )
    }

    private fun validateItem(item: ParserImportItemDto, index: Int): ImportErrorItemDto? {
        val nameTag = item.name.take(256)
        val urlTag = item.url.take(256)

        return when {
            item.name.isBlank() ->
                ImportErrorItemDto(index, urlTag, null, "name не может быть пустым")

            item.url.isBlank() ->
                ImportErrorItemDto(index, null, nameTag, "url не может быть пустым")

            item.url.length > 1024 ->
                ImportErrorItemDto(index, urlTag, nameTag, "url слишком длинный (max 1024)")

            !isValidAbsoluteUrl(item.url.trim()) ->
                ImportErrorItemDto(index, urlTag, nameTag, "url должен быть абсолютным http/https URL")

            item.name.length > 512 ->
                ImportErrorItemDto(index, urlTag, nameTag, "name слишком длинный (max 512)")

            item.price < 0 ->
                ImportErrorItemDto(index, urlTag, nameTag, "price не может быть отрицательным")

            item.currency.isBlank() ->
                ImportErrorItemDto(index, urlTag, nameTag, "currency не может быть пустым")

            else -> validateVariants(item, index)
        }
    }

    private fun validateVariants(item: ParserImportItemDto, index: Int): ImportErrorItemDto? {
        val nameTag = item.name.take(256)
        val urlTag = item.url.take(256)

        item.variants.forEachIndexed { vi, v ->
            if (v.weight != null && v.weight <= 0) {
                return ImportErrorItemDto(
                    index, urlTag, nameTag,
                    "variant[$vi].weight должен быть > 0 (получено: ${v.weight})"
                )
            }
            val n = v.nutrients ?: return@forEachIndexed
            if (n.calories != null && n.calories < 0)
                return ImportErrorItemDto(index, urlTag, nameTag, "variant[$vi].calories должен быть >= 0")
            if (n.protein != null && n.protein < 0)
                return ImportErrorItemDto(index, urlTag, nameTag, "variant[$vi].protein должен быть >= 0")
            if (n.fat != null && n.fat < 0)
                return ImportErrorItemDto(index, urlTag, nameTag, "variant[$vi].fat должен быть >= 0")
            if (n.carbs != null && n.carbs < 0)
                return ImportErrorItemDto(index, urlTag, nameTag, "variant[$vi].carbs должен быть >= 0")
        }
        return null
    }

    private fun isValidAbsoluteUrl(url: String): Boolean =
        try {
            val uri = URI(url)
            uri.isAbsolute && uri.scheme in setOf("http", "https")
        } catch (_: Exception) {
            false
        }
}
