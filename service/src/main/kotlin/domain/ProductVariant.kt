package org.example.domain

data class ProductVariant(
    val id: Long,
    val productId: Long,
    val manufacturer: String? = null,
    val composition: String? = null,
    val weight: Int? = null,
    val nutrients: Nutrients = Nutrients()
)
