package org.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ParserImportRequest(
    val deliveryServiceCode: String,
    val items: List<ParserImportItemDto> = emptyList()
)

@Serializable
data class ParserImportItemDto(
    val id: Long? = null,
    val name: String,
    val url: String,
    val price: Int,
    val currency: String = "RUB",
    val deliveryService: ParserDeliveryServiceDto? = null,
    val variants: List<ParserImportVariantDto> = emptyList()
)

@Serializable
data class ParserDeliveryServiceDto(
    val id: Long? = null,
    val code: String? = null,
    val name: String? = null,
    val siteUrl: String? = null,
    val logoUrl: String? = null,
    val active: Boolean = true
)

@Serializable
data class ParserImportVariantDto(
    val id: Long? = null,
    val manufacturer: String? = null,
    val composition: String? = null,
    val weight: Int? = null,
    val nutrients: ParserImportNutrientsDto? = null
)

@Serializable
data class ParserImportNutrientsDto(
    val calories: Int? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbs: Double? = null
)
