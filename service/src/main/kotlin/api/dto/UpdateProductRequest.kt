package org.example.api.dto

import kotlinx.serialization.Serializable

/**
 * Partial update request for a product and its first variant.
 * Null fields are ignored (not updated). To clear a string, pass "".
 */
@Serializable
data class UpdateProductRequest(
    val name: String? = null,
    val price: Int? = null,
    // first variant fields
    val manufacturer: String? = null,
    val composition: String? = null,
    val weight: Int? = null,
    val calories: Int? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbs: Double? = null
)
