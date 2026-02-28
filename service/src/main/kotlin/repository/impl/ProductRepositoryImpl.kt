package org.example.repository.impl

import org.example.config.DatabaseFactory
import org.example.domain.*
import org.example.repository.*
import org.example.service.model.Page
import org.example.service.model.ProductSearchCriteria
import org.example.service.model.SortDirection
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inSubQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like

object ProductRepositoryImpl : ProductRepository {

    /** При поиске по q результаты ранжируются в памяти; учитывается не более N записей. */
    private const val MAX_SEARCH_RESULTS_FOR_RANKING = 2000

    override suspend fun findById(id: Long): Product? =
        DatabaseFactory.dbQuery {
            val productRow = ProductsTable
                .selectAll()
                .where { ProductsTable.id eq id }
                .limit(1)
                .firstOrNull()
                ?: return@dbQuery null

            val serviceRow = DeliveryServicesTable
                .selectAll()
                .where { DeliveryServicesTable.id eq productRow[ProductsTable.deliveryServiceId] }
                .limit(1)
                .first()

            val variantRows = ProductVariantsTable
                .selectAll()
                .where { ProductVariantsTable.productId eq id }
                .toList()

            val service = serviceRow.toDeliveryService()
            val variants = variantRows.map { it.toProductVariant() }

            Product(
                id = id,
                name = productRow[ProductsTable.name],
                url = productRow[ProductsTable.url],
                price = productRow[ProductsTable.price],
                currency = productRow[ProductsTable.currency],
                deliveryService = service,
                variants = variants
            )
        }

    override suspend fun existsByDeliveryServiceId(deliveryServiceId: Long): Boolean =
        DatabaseFactory.dbQuery {
            ProductsTable
                .slice(ProductsTable.id)
                .select { ProductsTable.deliveryServiceId eq deliveryServiceId }
                .limit(1)
                .firstOrNull() != null
        }

    override suspend fun search(criteria: ProductSearchCriteria): Page<Product> =
        DatabaseFactory.dbQuery {
            val whereOp = buildProductWhere(criteria)

            val totalCountExpr = ProductsTable.id.count()
            val totalElements = ProductsTable
                .select(totalCountExpr)
                .where { whereOp }
                .first()[totalCountExpr]

            val hasSearchQuery = criteria.query?.trim()?.isNotEmpty() == true
            if (hasSearchQuery) {
                return@dbQuery searchWithRanking(criteria, whereOp, totalElements)
            }

            var query: Query = (ProductsTable innerJoin DeliveryServicesTable)
                .selectAll()
                .where { whereOp }

            val order = buildOrderBy(criteria)
            if (order != null) {
                query = query.orderBy(order)
            } else {
                query = query.orderBy(ProductsTable.name to SortOrder.ASC)
            }

            val limit = criteria.size
            val offset = (criteria.page * criteria.size).toLong()

            val rows = query
                .limit(limit, offset)
                .toList()

            val productIds = rows.map { it[ProductsTable.id] }
            if (productIds.isEmpty()) {
                return@dbQuery Page(
                    items = emptyList(),
                    page = criteria.page,
                    size = criteria.size,
                    totalElements = totalElements
                )
            }

            val variantsWhere = buildVariantWhere(criteria, productIds)

            val variantRows = ProductVariantsTable
                .selectAll()
                .where { variantsWhere }
                .toList()

            val variantsByProductId = variantRows.groupBy { it[ProductVariantsTable.productId] }

            val products = rows.map { row ->
                val productId = row[ProductsTable.id]
                val service = row.toDeliveryService()
                val variants = variantsByProductId[productId]?.map { it.toProductVariant() }.orEmpty()

                Product(
                    id = productId,
                    name = row[ProductsTable.name],
                    url = row[ProductsTable.url],
                    price = row[ProductsTable.price],
                    currency = row[ProductsTable.currency],
                    deliveryService = service,
                    variants = variants
                )
            }

            Page(
                items = products,
                page = criteria.page,
                size = criteria.size,
                totalElements = totalElements
            )
        }

    /**
     * Поиск с ранжированием: сначала точная фраза в названии, потом все слова в названии,
     * потом часть слов в названии, потом совпадения только в составе вариантов.
     */
    private fun searchWithRanking(
        criteria: ProductSearchCriteria,
        whereOp: Op<Boolean>,
        totalElements: Long
    ): Page<Product> {
        val words = criteria.query!!.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val fullPhrase = words.joinToString(" ")
        val cappedTotal = minOf(totalElements, MAX_SEARCH_RESULTS_FOR_RANKING.toLong())

        val rows = (ProductsTable innerJoin DeliveryServicesTable)
            .selectAll()
            .where { whereOp }
            .orderBy(ProductsTable.name to SortOrder.ASC)
            .limit(MAX_SEARCH_RESULTS_FOR_RANKING, 0)
            .toList()

        val productIds = rows.map { it[ProductsTable.id] }
        if (productIds.isEmpty()) {
            return Page(
                items = emptyList(),
                page = criteria.page,
                size = criteria.size,
                totalElements = cappedTotal
            )
        }

        val variantsWhere = buildVariantWhere(criteria, productIds)
        val variantRows = ProductVariantsTable
            .selectAll()
            .where { variantsWhere }
            .toList()
        val variantsByProductId = variantRows.groupBy { it[ProductVariantsTable.productId] }

        val products = rows.map { row ->
            val productId = row[ProductsTable.id]
            val service = row.toDeliveryService()
            val variants = variantsByProductId[productId]?.map { it.toProductVariant() }.orEmpty()
            Product(
                id = productId,
                name = row[ProductsTable.name],
                url = row[ProductsTable.url],
                price = row[ProductsTable.price],
                currency = row[ProductsTable.currency],
                deliveryService = service,
                variants = variants
            )
        }

        val sorted = products
            .map { p -> p to computeSearchRank(p.name, p.variants.mapNotNull { it.composition }, words, fullPhrase) }
            .sortedWith(compareByDescending<Pair<Product, Int>> { it.second }.thenBy { it.first.name })
            .map { it.first }

        val offset = criteria.page * criteria.size
        val pageItems = sorted.drop(offset).take(criteria.size)

        return Page(
            items = pageItems,
            page = criteria.page,
            size = criteria.size,
            totalElements = cappedTotal
        )
    }

    /**
     * Ранг релевантности: 100 — точная фраза в названии, 80 — все слова в названии,
     * 40 — часть слов в названии, 20 — только в составе/описании вариантов.
     */
    private fun computeSearchRank(
        productName: String,
        variantCompositions: List<String>,
        words: List<String>,
        fullPhrase: String
    ): Int {
        val nameLower = productName.lowercase()
        val phraseInName = nameLower.contains(fullPhrase)
        val allWordsInName = words.all { nameLower.contains(it) }
        val anyWordInName = words.any { nameLower.contains(it) }
        val allWordsInComposition = variantCompositions.any { comp ->
            val c = comp.lowercase()
            words.all { c.contains(it) }
        }

        return when {
            phraseInName -> 100
            allWordsInName -> 80
            anyWordInName -> 40
            allWordsInComposition -> 20
            else -> 0
        }
    }

    private fun buildProductWhere(criteria: ProductSearchCriteria): Op<Boolean> {
        val conditions = mutableListOf<Op<Boolean>>()

        criteria.query?.trim()?.takeIf { it.isNotEmpty() }?.let { raw ->
            val words = raw.lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
            for (word in words) {
                val nameMatch = lower(ProductsTable.name) like "%$word%"
                val compositionMatch = ProductsTable.id inSubQuery (
                    ProductVariantsTable.slice(ProductVariantsTable.productId).select {
                        ProductVariantsTable.composition.isNotNull() and
                            (lowerCoalesce(ProductVariantsTable.composition) like "%$word%")
                    }
                )
                conditions += (nameMatch or compositionMatch)
            }
        }

        criteria.deliveryServiceIds?.takeIf { it.isNotEmpty() }?.let { ids ->
            conditions += (ProductsTable.deliveryServiceId inList ids)
        }

        if (criteria.hasNutrientFilters()) {
            val variantCond = buildVariantNutrientsOnlyWhere(criteria)
            val subQuery = ProductVariantsTable
                .select(ProductVariantsTable.productId)
                .where { variantCond }
                .withDistinct()

            conditions += (ProductsTable.id inSubQuery subQuery)
        }

        return andAll(conditions)
    }

    private fun buildVariantWhere(criteria: ProductSearchCriteria, productIds: List<Long>): Op<Boolean> {
        var op: Op<Boolean> = (ProductVariantsTable.productId inList productIds)

        if (criteria.hasNutrientFilters()) {
            op = op and buildVariantNutrientsOnlyWhere(criteria)
        }

        return op
    }

    private fun buildVariantNutrientsOnlyWhere(criteria: ProductSearchCriteria): Op<Boolean> {
        val conds = mutableListOf<Op<Boolean>>()

        criteria.minCalories?.let { conds += ProductVariantsTable.calories greaterEq it }
        criteria.maxCalories?.let { conds += ProductVariantsTable.calories lessEq it }

        criteria.minProtein?.let { conds += ProductVariantsTable.protein greaterEq it }
        criteria.maxProtein?.let { conds += ProductVariantsTable.protein lessEq it }

        criteria.minFat?.let { conds += ProductVariantsTable.fat greaterEq it }
        criteria.maxFat?.let { conds += ProductVariantsTable.fat lessEq it }

        criteria.minCarbs?.let { conds += ProductVariantsTable.carbs greaterEq it }
        criteria.maxCarbs?.let { conds += ProductVariantsTable.carbs lessEq it }

        return andAll(conds)
    }

    private fun andAll(conds: List<Op<Boolean>>): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE
        for (c in conds) op = op and c
        return op
    }

    private fun lower(expr: Expression<String>): Expression<String> =
        CustomFunction("LOWER", TextColumnType(), expr)

    /** LOWER(COALESCE(expr, '')) для nullable-колонок (composition). */
    private fun lowerCoalesce(expr: Expression<String?>): Expression<String> =
        lower(CustomFunction("COALESCE", TextColumnType(), expr, stringLiteral("")))

    private fun buildOrderBy(criteria: ProductSearchCriteria): Pair<Expression<*>, SortOrder>? {
        val sort = criteria.sort ?: return null
        val order = if (sort.direction == SortDirection.ASC) SortOrder.ASC else SortOrder.DESC

        return when (sort.field.lowercase()) {
            "name" -> ProductsTable.name to order
            "price" -> ProductsTable.price to order
            else -> null
        }
    }

    private fun ResultRow.toDeliveryService(): DeliveryService =
        DeliveryService(
            id = this[DeliveryServicesTable.id],
            code = this[DeliveryServicesTable.code],
            name = this[DeliveryServicesTable.name],
            siteUrl = this[DeliveryServicesTable.siteUrl],
            logoUrl = this[DeliveryServicesTable.logoUrl],
            active = this[DeliveryServicesTable.active]
        )

    private fun ResultRow.toProductVariant(): ProductVariant =
        ProductVariant(
            id = this[ProductVariantsTable.id],
            productId = this[ProductVariantsTable.productId],
            manufacturer = this[ProductVariantsTable.manufacturer],
            composition = this[ProductVariantsTable.composition],
            weight = this[ProductVariantsTable.weight],
            nutrients = Nutrients(
                calories = this[ProductVariantsTable.calories],
                protein = this[ProductVariantsTable.protein],
                fat = this[ProductVariantsTable.fat],
                carbs = this[ProductVariantsTable.carbs]
            )
        )
}
