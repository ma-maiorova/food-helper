package org.example.repository.impl

import org.example.api.dto.UpdateProductRequest
import org.example.config.DatabaseFactory
import org.example.domain.*
import org.example.repository.*
import org.example.service.computeSearchRank
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
import java.time.Instant

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

            val orders = buildOrderBy(criteria)
            query = if (orders.isNotEmpty()) {
                query.orderBy(*orders.toTypedArray())
            } else {
                // Default: name ASC, id ASC for stability
                query.orderBy(ProductsTable.name to SortOrder.ASC, ProductsTable.id to SortOrder.ASC)
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
     * Поиск с ранжированием: exact phrase (100) > all tokens in name (80) >
     * mixed name+composition (50) > all tokens only in composition (30).
     * Tie-break: rank DESC, name ASC (case-insensitive), id ASC — стабильная сортировка.
     */
    private fun searchWithRanking(
        criteria: ProductSearchCriteria,
        whereOp: Op<Boolean>,
        totalElements: Long
    ): Page<Product> {
        val tokens = criteria.query!!.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val fullPhrase = tokens.joinToString(" ")
        val cappedTotal = minOf(totalElements, MAX_SEARCH_RESULTS_FOR_RANKING.toLong())

        // Fetch candidates ordered by name+id for deterministic DB reads
        val rows = (ProductsTable innerJoin DeliveryServicesTable)
            .selectAll()
            .where { whereOp }
            .orderBy(ProductsTable.name to SortOrder.ASC, ProductsTable.id to SortOrder.ASC)
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

        // rank DESC, name ASC (case-insensitive), id ASC — deterministic for pagination
        val sorted = products
            .map { p -> p to computeSearchRank(p.name, p.variants.mapNotNull { it.composition }, tokens, fullPhrase) }
            .sortedWith(
                compareByDescending<Pair<Product, Int>> { it.second }
                    .thenBy { it.first.name.lowercase() }
                    .thenBy { it.first.id }
            )
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

    private fun buildProductWhere(criteria: ProductSearchCriteria): Op<Boolean> {
        val conditions = mutableListOf<Op<Boolean>>()

        // AND semantics: each token must match product.name OR some variant.composition
        criteria.query?.trim()?.takeIf { it.isNotEmpty() }?.let { raw ->
            val tokens = raw.lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
            for (token in tokens) {
                val nameMatch = lower(ProductsTable.name) like "%$token%"
                val compositionMatch = ProductsTable.id inSubQuery (
                    ProductVariantsTable.slice(ProductVariantsTable.productId).select {
                        ProductVariantsTable.composition.isNotNull() and
                            (lowerCoalesce(ProductVariantsTable.composition) like "%$token%")
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

    /**
     * Returns ORDER BY pairs including deterministic id ASC tie-breaker.
     * Empty list means "no explicit sort requested" — caller applies default (name, id).
     */
    private fun buildOrderBy(criteria: ProductSearchCriteria): List<Pair<Expression<*>, SortOrder>> {
        val sort = criteria.sort ?: return emptyList()
        val order = if (sort.direction == SortDirection.ASC) SortOrder.ASC else SortOrder.DESC

        return when (sort.field.lowercase()) {
            "name" -> listOf(ProductsTable.name to order, ProductsTable.id to SortOrder.ASC)
            "price" -> listOf(ProductsTable.price to order, ProductsTable.id to SortOrder.ASC)
            else -> emptyList()
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

    // ─── Admin: delete ────────────────────────────────────────────────────────

    override suspend fun deleteById(id: Long): Boolean =
        DatabaseFactory.dbQuery {
            val count = ProductsTable.deleteWhere { ProductsTable.id eq id }
            count > 0
        }

    // ─── Admin: update ────────────────────────────────────────────────────────

    override suspend fun updateProduct(id: Long, req: UpdateProductRequest): Product? =
        DatabaseFactory.dbQuery {
            val hasProductChanges = req.name != null || req.price != null
            if (hasProductChanges) {
                ProductsTable.update({ ProductsTable.id eq id }) { stmt ->
                    req.name?.let { stmt[name] = it }
                    req.price?.let { stmt[price] = it }
                    stmt[updatedAt] = Instant.now()
                }
            }

            val hasVariantChanges = req.manufacturer != null || req.composition != null ||
                req.weight != null || req.calories != null ||
                req.protein != null || req.fat != null || req.carbs != null
            if (hasVariantChanges) {
                val variantId = ProductVariantsTable
                    .select(ProductVariantsTable.id)
                    .where { ProductVariantsTable.productId eq id }
                    .orderBy(ProductVariantsTable.id, SortOrder.ASC)
                    .limit(1)
                    .firstOrNull()
                    ?.get(ProductVariantsTable.id)

                if (variantId != null) {
                    ProductVariantsTable.update({ ProductVariantsTable.id eq variantId }) { stmt ->
                        req.manufacturer?.let { stmt[manufacturer] = it.ifEmpty { null } }
                        req.composition?.let { stmt[composition] = it.ifEmpty { null } }
                        req.weight?.let { stmt[weight] = it }
                        req.calories?.let { stmt[calories] = it }
                        req.protein?.let { stmt[protein] = it }
                        req.fat?.let { stmt[fat] = it }
                        req.carbs?.let { stmt[carbs] = it }
                    }
                }
            }

            val productRow = ProductsTable.selectAll()
                .where { ProductsTable.id eq id }
                .firstOrNull() ?: return@dbQuery null

            val serviceRow = DeliveryServicesTable.selectAll()
                .where { DeliveryServicesTable.id eq productRow[ProductsTable.deliveryServiceId] }
                .first()

            val variantRows = ProductVariantsTable.selectAll()
                .where { ProductVariantsTable.productId eq id }
                .toList()

            Product(
                id = id,
                name = productRow[ProductsTable.name],
                url = productRow[ProductsTable.url],
                price = productRow[ProductsTable.price],
                currency = productRow[ProductsTable.currency],
                deliveryService = serviceRow.toDeliveryService(),
                variants = variantRows.map { it.toProductVariant() }
            )
        }
}
