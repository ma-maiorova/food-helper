package org.example.service.model

data class Page<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long
) {
    val totalPages: Int =
        if (size <= 0) 0 else ((totalElements + size - 1) / size).toInt()
}
