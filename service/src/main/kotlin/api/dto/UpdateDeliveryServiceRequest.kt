package org.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDeliveryServiceRequest(
    val name: String? = null,
    val siteUrl: String? = null,
    val logoUrl: String? = null,
    val active: Boolean? = null
)
