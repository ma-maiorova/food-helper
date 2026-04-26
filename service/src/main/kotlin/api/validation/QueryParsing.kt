package org.example.api.validation

import io.ktor.server.application.*
import org.example.api.errors.ValidationException
import org.example.service.model.ProductSearchCriteria
import org.example.service.model.SortDirection
import org.example.service.model.SortSpec

fun parseProductSearchCriteria(call: ApplicationCall): ProductSearchCriteria {
    val qp = call.request.queryParameters

    val q = qp["q"]?.trim()

    val deliveryServiceIds = parseLongList(qp["deliveryServiceIds"])

    val page = qp["page"]?.toIntOrNull() ?: 0
    val size = qp["size"]?.toIntOrNull() ?: 20

    val sort = qp["sort"]?.let { parseSort(it) }

    val userAgent = call.request.headers["User-Agent"] ?: ""
    val source = if (userAgent.startsWith("food-helper-bot")) "bot" else "web"

    val criteria = ProductSearchCriteria(
        query = q,
        deliveryServiceIds = deliveryServiceIds,
        source = source,
        minCalories = qp["minCalories"]?.toIntOrNull(),
        maxCalories = qp["maxCalories"]?.toIntOrNull(),
        minProtein = qp["minProtein"]?.toDoubleOrNull(),
        maxProtein = qp["maxProtein"]?.toDoubleOrNull(),
        minFat = qp["minFat"]?.toDoubleOrNull(),
        maxFat = qp["maxFat"]?.toDoubleOrNull(),
        minCarbs = qp["minCarbs"]?.toDoubleOrNull(),
        maxCarbs = qp["maxCarbs"]?.toDoubleOrNull(),
        page = page,
        size = size,
        sort = sort
    )

    validate(criteria)
    return criteria
}

private fun parseLongList(raw: String?): List<Long>? {
    if (raw.isNullOrBlank()) return null
    return raw.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map {
            it.toLongOrNull() ?: throw ValidationException("deliveryServiceIds must be a comma-separated list of integers")
        }
        .takeIf { it.isNotEmpty() }
}

private fun parseSort(raw: String): SortSpec {
    val parts = raw.split(",").map { it.trim() }
    if (parts.isEmpty() || parts[0].isEmpty()) throw ValidationException("sort must be like 'name,asc' or 'price,desc'")

    val field = parts[0]
    val direction = parts.getOrNull(1)?.lowercase()?.let {
        when (it) {
            "asc" -> SortDirection.ASC
            "desc" -> SortDirection.DESC
            else -> throw ValidationException("sort direction must be 'asc' or 'desc'")
        }
    } ?: SortDirection.ASC

    val allowed = setOf("name", "price")
    if (field.lowercase() !in allowed) {
        throw ValidationException("sort field must be one of: name, price")
    }

    return SortSpec(field = field, direction = direction)
}

private fun validate(c: ProductSearchCriteria) {
    if (c.page < 0) throw ValidationException("page must be >= 0")
    if (c.size !in 1..100) throw ValidationException("size must be between 1 and 100")

    fun rangeInt(min: Int?, max: Int?, name: String) {
        if (min != null && max != null && max < min) {
            throw ValidationException("$name must be >= $name(min)")
        }
    }
    fun rangeDouble(min: Double?, max: Double?, name: String) {
        if (min != null && max != null && max < min) {
            throw ValidationException("$name(max) must be >= $name(min)")
        }
    }

    rangeInt(c.minCalories, c.maxCalories, "calories")
    rangeDouble(c.minProtein, c.maxProtein, "protein")
    rangeDouble(c.minFat, c.maxFat, "fat")
    rangeDouble(c.minCarbs, c.maxCarbs, "carbs")
}
