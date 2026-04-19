package org.example.domain

data class Product(
    val id: Long,
    val name: String,
    val url: String,
    val price: Int,
    val currency: String = "RUB",
    val deliveryService: DeliveryService,
    val variants: List<ProductVariant> = emptyList()
)
