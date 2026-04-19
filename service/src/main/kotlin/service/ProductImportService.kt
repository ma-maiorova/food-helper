package org.example.service

import org.example.api.dto.ImportErrorCode
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
     * **Request-level checks** (→ 400 whole request fails):
     * - items.size must be <= [maxItemsPerRequest]
     * - items must not be empty
     * - deliveryServiceCode must not be blank
     *
     * **Item-level checks** (→ failed item in response, rest continue):
     * - name not blank, ≤ 512 chars
     * - url not blank, absolute http/https, ≤ 1024 chars
     * - price ≥ 0, currency not blank
     * - variants must not be empty
     * - weight > 0 if provided; nutrient values ≥ 0 if provided
     *
     * **Duplicate URL in batch** → last wins. Earlier occurrences of the same URL
     * are silently dropped before processing; [ImportResultDto.duplicatesResolved] reflects
     * how many were dropped.
     *
     * **Duplicate variants in item** → all kept as-is (no dedup). The parser is
     * responsible for providing a clean variant list.
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

        val totalReceived = request.items.size

        // Deduplicate by URL within batch — last wins.
        // Build URL → last index map, then keep only the last occurrence of each URL.
        val urlToLastIndex = mutableMapOf<String, Int>()
        request.items.forEachIndexed { i, item ->
            urlToLastIndex[item.url.trim()] = i
        }
        val dedupedItems = request.items.filterIndexed { i, item ->
            urlToLastIndex[item.url.trim()] == i
        }
        val duplicatesResolved = totalReceived - dedupedItems.size

        AppMetrics.importRequestsTotal(code).increment()
        AppMetrics.importItemsTotal(code).increment(totalReceived.toDouble())
        AppMetrics.importBatchSize.record(totalReceived.toDouble())
        if (duplicatesResolved > 0) {
            AppMetrics.importDuplicateOverriddenTotal(code).increment(duplicatesResolved.toDouble())
        }

        log.info(
            "import.start deliveryServiceCode={} itemsCount={} duplicatesResolved={}",
            code, totalReceived, duplicatesResolved
        )

        val startNs = System.nanoTime()
        val deliveryService = deliveryServiceRepository.findByCode(code)
            ?: run {
                val errorCode = ImportErrorCode.UNKNOWN_SERVICE
                AppMetrics.importFailedItemsTotal(code, errorCode).increment(dedupedItems.size.toDouble())
                log.warn("import.finish deliveryServiceCode={} status=unknown_service", code)
                return ImportResultDto(
                    totalReceived = totalReceived,
                    duplicatesResolved = duplicatesResolved,
                    created = 0,
                    updated = 0,
                    failed = dedupedItems.size,
                    durationMs = (System.nanoTime() - startNs) / 1_000_000,
                    errors = listOf(
                        ImportErrorItemDto(
                            itemIndex = -1,
                            errorCode = errorCode,
                            message = "Служба доставки с кодом '$code' не найдена"
                        )
                    )
                )
            }

        val errors = mutableListOf<ImportErrorItemDto>()
        var createdCount = 0
        var updatedCount = 0

        val chunks = dedupedItems.chunked(chunkSize.coerceAtLeast(1))
        chunks.forEachIndexed { chunkIndex, chunk ->
            chunk.forEachIndexed { indexInChunk, item ->
                val globalIndex = chunkIndex * chunkSize + indexInChunk
                val validationError = validateItem(item, globalIndex)
                if (validationError != null) {
                    AppMetrics.importFailedItemsTotal(code, validationError.errorCode).increment()
                    errors.add(validationError)
                    return@forEachIndexed
                }
                try {
                    val trimmedName = item.name.trim()
                    val trimmedUrl = item.url.trim()
                    val trimmedCurrency = item.currency.trim().take(8)

                    val upsertResult = productImportRepository.upsertProduct(
                        deliveryServiceId = deliveryService.id,
                        name = trimmedName,
                        url = trimmedUrl,
                        price = item.price,
                        currency = trimmedCurrency
                    )
                    productImportRepository.replaceVariants(upsertResult.productId, item.variants)
                    if (upsertResult.created) {
                        createdCount++
                        AppMetrics.importCreatedItemsTotal(code).increment()
                    } else {
                        updatedCount++
                        AppMetrics.importUpdatedItemsTotal(code).increment()
                    }
                } catch (e: Exception) {
                    val errorCode = ImportErrorCode.INTERNAL_ERROR
                    log.warn(
                        "import.item_error index={} url={} errorCode={} error={}",
                        globalIndex, item.url.take(256), errorCode, e.message
                    )
                    AppMetrics.importFailedItemsTotal(code, errorCode).increment()
                    errors.add(
                        ImportErrorItemDto(
                            itemIndex = globalIndex,
                            url = item.url.take(256),
                            name = item.name.take(256),
                            errorCode = errorCode,
                            message = e.message ?: e.javaClass.simpleName
                        )
                    )
                }
            }
        }

        val durationMs = (System.nanoTime() - startNs) / 1_000_000
        AppMetrics.importDuration(code).record(System.nanoTime() - startNs, TimeUnit.NANOSECONDS)

        log.info(
            "import.finish deliveryServiceCode={} totalReceived={} duplicatesResolved={} created={} updated={} failed={} durationMs={}",
            code, totalReceived, duplicatesResolved, createdCount, updatedCount, errors.size, durationMs
        )

        return ImportResultDto(
            totalReceived = totalReceived,
            duplicatesResolved = duplicatesResolved,
            created = createdCount,
            updated = updatedCount,
            failed = errors.size,
            durationMs = durationMs,
            errors = errors
        )
    }

    private fun validateItem(item: ParserImportItemDto, index: Int): ImportErrorItemDto? {
        val nameTag = item.name.take(256)
        val urlTag = item.url.take(256)

        return when {
            item.name.isBlank() ->
                ImportErrorItemDto(index, urlTag, null, ImportErrorCode.BLANK_NAME, "name не может быть пустым")

            item.url.isBlank() ->
                ImportErrorItemDto(index, null, nameTag, ImportErrorCode.BLANK_URL, "url не может быть пустым")

            item.url.length > 1024 ->
                ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.URL_TOO_LONG, "url слишком длинный (max 1024)")

            !isValidAbsoluteUrl(item.url.trim()) ->
                ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.INVALID_URL, "url должен быть абсолютным http/https URL")

            item.name.length > 512 ->
                ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.NAME_TOO_LONG, "name слишком длинный (max 512)")

            item.price < 0 ->
                ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.NEGATIVE_PRICE, "price не может быть отрицательным")

            item.currency.isBlank() ->
                ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.BLANK_CURRENCY, "currency не может быть пустым")

            item.variants.isEmpty() ->
                ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.EMPTY_VARIANTS, "variants не могут быть пустыми")

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
                    ImportErrorCode.INVALID_WEIGHT,
                    "variant[$vi].weight должен быть > 0 (получено: ${v.weight})"
                )
            }
            val n = v.nutrients ?: return@forEachIndexed
            if (n.calories != null && n.calories < 0)
                return ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.NEGATIVE_NUTRIENTS, "variant[$vi].calories должен быть >= 0")
            if (n.protein != null && n.protein < 0)
                return ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.NEGATIVE_NUTRIENTS, "variant[$vi].protein должен быть >= 0")
            if (n.fat != null && n.fat < 0)
                return ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.NEGATIVE_NUTRIENTS, "variant[$vi].fat должен быть >= 0")
            if (n.carbs != null && n.carbs < 0)
                return ImportErrorItemDto(index, urlTag, nameTag, ImportErrorCode.NEGATIVE_NUTRIENTS, "variant[$vi].carbs должен быть >= 0")
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
