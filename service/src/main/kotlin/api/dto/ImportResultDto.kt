package org.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImportResultDto(
    val importedCount: Int,
    val failedCount: Int,
    val errors: List<ImportErrorItemDto> = emptyList()
)

@Serializable
data class ImportErrorItemDto(
    val itemIndex: Int,
    val url: String? = null,
    val name: String? = null,
    val message: String
)
