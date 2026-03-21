package org.example.repository

import org.example.api.dto.ParserImportVariantDto

/** Результат upsert-операции: продукт был создан или обновлён. */
data class UpsertResult(val productId: Long, val created: Boolean)

interface ProductImportRepository {
    /**
     * Возвращает результат upsert: id продукта и флаг created/updated.
     * Идемпотентно: при совпадении (delivery_service_id, url) обновляет name, price, currency.
     */
    suspend fun upsertProduct(
        deliveryServiceId: Long,
        name: String,
        url: String,
        price: Int,
        currency: String
    ): UpsertResult

    /**
     * Удаляет все варианты продукта и вставляет переданные.
     * Обновляет нутриенты (calories, protein, fat, carbs) по новым значениям.
     */
    suspend fun replaceVariants(productId: Long, variants: List<ParserImportVariantDto>)
}
