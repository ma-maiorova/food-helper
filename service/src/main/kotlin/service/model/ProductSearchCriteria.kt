package org.example.service.model

data class ProductSearchCriteria(
    val query: String? = null,
    val deliveryServiceIds: List<Long>? = null,

    val minCalories: Int? = null,
    val maxCalories: Int? = null,
    val minProtein: Double? = null,
    val maxProtein: Double? = null,
    val minFat: Double? = null,
    val maxFat: Double? = null,
    val minCarbs: Double? = null,
    val maxCarbs: Double? = null,

    /** When true, nutrient filters are applied to the whole dish (value × weight / 100)
     *  instead of per 100 g. Variants without weight are excluded from results. */
    val perDish: Boolean = false,

    val page: Int = 0,
    val size: Int = 20,
    val sort: SortSpec? = null
) {
    fun hasNutrientFilters(): Boolean =
        minCalories != null || maxCalories != null ||
            minProtein != null || maxProtein != null ||
            minFat != null || maxFat != null ||
            minCarbs != null || maxCarbs != null
}
