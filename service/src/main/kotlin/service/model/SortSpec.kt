package org.example.service.model

enum class SortDirection { ASC, DESC }

data class SortSpec(
    val field: String,
    val direction: SortDirection
)
