package org.example.repository

import org.example.api.dto.ParserImportVariantDto

interface ProductImportRepository {
    /**
     * Возвращает id продукта (существующего или только что созданного).
     * Идемпотентно: при совпадении (delivery_service_id, url) обновляет name, price, currency.
     */
    suspend fun upsertProduct(
        deliveryServiceId: Long,
        name: String,
        url: String,
        price: Int,
        currency: String
    ): Long

    /**
     * Удаляет все варианты продукта и вставляет переданные.
     * Обновляет нутриенты (calories, protein, fat, carbs) по новым значениям.
     */
    suspend fun replaceVariants(productId: Long, variants: List<ParserImportVariantDto>)
}
