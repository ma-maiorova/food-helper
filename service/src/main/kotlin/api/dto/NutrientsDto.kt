package org.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class NutrientsDto(
    val calories: Int? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbs: Double? = null
)
