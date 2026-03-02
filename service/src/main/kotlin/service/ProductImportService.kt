package org.example.service

import org.example.api.dto.ImportErrorItemDto
import org.example.api.dto.ImportResultDto
import org.example.api.dto.ParserImportItemDto
import org.example.api.dto.ParserImportRequest
import org.example.repository.DeliveryServiceRepository
import org.example.repository.ProductImportRepository

class ProductImportService(
    private val deliveryServiceRepository: DeliveryServiceRepository,
    private val productImportRepository: ProductImportRepository,
    private val maxItemsPerRequest: Int,
    private val chunkSize: Int
) {
    /**
     * Импортирует батч продуктов от парсера.
     * За один запрос обрабатывается не более [maxItemsPerRequest] элементов; при большем числе
     * берутся первые N. Элементы разбиваются на внутренние батчи по [chunkSize] и обрабатываются
     * по батчам. Каждый элемент — в отдельной короткой транзакции; ошибки по элементам собираются.
     */
    suspend fun importBatch(request: ParserImportRequest): ImportResultDto {
        val code = request.deliveryServiceCode.trim()
        if (code.isEmpty()) {
            return ImportResultDto(
                importedCount = 0,
                failedCount = request.items.size,
                errors = listOf(
                    ImportErrorItemDto(itemIndex = -1, message = "deliveryServiceCode не может быть пустым")
                )
            )
        }

        val deliveryService = deliveryServiceRepository.findByCode(code)
            ?: return ImportResultDto(
                importedCount = 0,
                failedCount = request.items.size,
                errors = listOf(
                    ImportErrorItemDto(itemIndex = -1, message = "Служба доставки с кодом '$code' не найдена")
                )
            )

        val items = request.items.take(maxItemsPerRequest)
        val errors = mutableListOf<ImportErrorItemDto>()
        var importedCount = 0

        val chunks = items.chunked(chunkSize.coerceAtLeast(1))
        chunks.forEachIndexed { chunkIndex, chunk ->
            chunk.forEachIndexed { indexInChunk, item ->
                val globalIndex = chunkIndex * chunkSize + indexInChunk
                val validationError = validateItem(item, globalIndex)
                if (validationError != null) {
                    errors.add(validationError)
                    return@forEachIndexed
                }
                try {
                    val productId = productImportRepository.upsertProduct(
                        deliveryServiceId = deliveryService.id,
                        name = item.name.trim(),
                        url = item.url.trim(),
                        price = item.price,
                        currency = item.currency.take(8).ifEmpty { "RUB" }
                    )
                    productImportRepository.replaceVariants(productId, item.variants)
                    importedCount++
                } catch (e: Exception) {
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

        val failedCount = errors.size
        return ImportResultDto(
            importedCount = importedCount,
            failedCount = failedCount,
            errors = errors
        )
    }

    private fun validateItem(item: ParserImportItemDto, index: Int): ImportErrorItemDto? {
        when {
            item.name.isBlank() -> return ImportErrorItemDto(index, item.url.take(256), item.name.take(256), "name не может быть пустым")
            item.url.isBlank() -> return ImportErrorItemDto(index, null, item.name.take(256), "url не может быть пустым")
            item.url.length > 1024 -> return ImportErrorItemDto(index, item.url.take(256), item.name.take(256), "url слишком длинный")
            item.name.length > 512 -> return ImportErrorItemDto(index, item.url.take(256), item.name.take(256), "name слишком длинный")
            item.price < 0 -> return ImportErrorItemDto(index, item.url.take(256), item.name.take(256), "price не может быть отрицательным")
        }
        return null
    }
}
