package org.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductVariantDto(
    val id: Long,
    val manufacturer: String? = null,
    val composition: String? = null,
    val weight: Int? = null,
    val nutrients: NutrientsDto
)
