package org.example.repository.impl

import org.example.api.dto.ParserImportVariantDto
import org.example.config.DatabaseFactory
import org.example.repository.ProductImportRepository
import org.example.repository.ProductVariantsTable
import org.example.repository.ProductsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

object ProductImportRepositoryImpl : ProductImportRepository {

    override suspend fun upsertProduct(
        deliveryServiceId: Long,
        name: String,
        url: String,
        price: Int,
        currency: String
    ): Long = DatabaseFactory.dbQuery {
        val existing = ProductsTable
            .selectAll()
            .where {
                (ProductsTable.deliveryServiceId eq deliveryServiceId) and (ProductsTable.url eq url)
            }
            .limit(1)
            .firstOrNull()

        val now = Instant.now()
        if (existing != null) {
            val id = existing[ProductsTable.id]
            ProductsTable.update({ ProductsTable.id eq id }) {
                it[ProductsTable.name] = name
                it[ProductsTable.price] = price
                it[ProductsTable.currency] = currency
                it[ProductsTable.updatedAt] = now
            }
            id
        } else {
            ProductsTable.insert {
                it[ProductsTable.deliveryServiceId] = deliveryServiceId
                it[ProductsTable.name] = name
                it[ProductsTable.url] = url
                it[ProductsTable.price] = price
                it[ProductsTable.currency] = currency
                it[ProductsTable.createdAt] = now
                it[ProductsTable.updatedAt] = now
            }
            ProductsTable
                .selectAll()
                .where {
                    (ProductsTable.deliveryServiceId eq deliveryServiceId) and (ProductsTable.url eq url)
                }
                .limit(1)
                .first()[ProductsTable.id]
        }
    }

    override suspend fun replaceVariants(productId: Long, variants: List<ParserImportVariantDto>) {
        DatabaseFactory.dbQuery {
            ProductVariantsTable.deleteWhere { ProductVariantsTable.productId eq productId }
            variants.forEach { v ->
                val n = v.nutrients
                ProductVariantsTable.insert {
                    it[ProductVariantsTable.productId] = productId
                    it[ProductVariantsTable.manufacturer] = v.manufacturer?.take(255)
                    it[ProductVariantsTable.composition] = v.composition
                    it[ProductVariantsTable.weight] = v.weight
                    it[ProductVariantsTable.calories] = n?.calories
                    it[ProductVariantsTable.protein] = n?.protein
                    it[ProductVariantsTable.fat] = n?.fat
                    it[ProductVariantsTable.carbs] = n?.carbs
                }
            }
        }
    }
}
