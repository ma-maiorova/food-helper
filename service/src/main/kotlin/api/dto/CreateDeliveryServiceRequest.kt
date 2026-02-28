package org.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateDeliveryServiceRequest(
    val code: String,
    val name: String,
    val siteUrl: String? = null,
    val logoUrl: String? = null,
    val active: Boolean = true
)
