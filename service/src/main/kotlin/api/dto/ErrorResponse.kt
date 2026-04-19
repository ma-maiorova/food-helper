package org.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class FieldError(
    val field: String,
    val message: String
)

@Serializable
data class ErrorResponse(
    val requestId: String,
    val code: String,
    val message: String,
    val status: Int,
    val path: String,
    val timestamp: String,
    val details: List<String>? = null,
    val fieldErrors: List<FieldError>? = null
)
