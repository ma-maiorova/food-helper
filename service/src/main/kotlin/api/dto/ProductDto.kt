package org.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Long,
    val name: String,
    val url: String,
    val price: Int,
    val currency: String = "RUB",
    val deliveryService: DeliveryServiceDto,
    val variants: List<ProductVariantDto> = emptyList()
)
