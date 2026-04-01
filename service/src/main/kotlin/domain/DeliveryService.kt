package org.example.domain

data class DeliveryService(
    val id: Long,
    val code: String,
    val name: String,
    val siteUrl: String? = null,
    val logoUrl: String? = null,
    val active: Boolean = true
)
