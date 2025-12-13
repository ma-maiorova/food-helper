package org.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val code: String,
    val message: String,
    val path: String
)
