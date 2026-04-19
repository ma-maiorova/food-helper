package org.example.repository

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object DeliveryServicesTable : Table("delivery_service") {
    val id = long("id").autoIncrement()
    val code = varchar("code", 64).uniqueIndex()
    val name = varchar("name", 255)
    val siteUrl = varchar("site_url", 512).nullable()
    val logoUrl = varchar("logo_url", 512).nullable()
    val active = bool("active").default(true)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object ProductsTable : Table("product") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 512)
    val url = varchar("url", 1024)
    val price = integer("price")
    val currency = varchar("currency", 8).default("RUB")
    val deliveryServiceId = long("delivery_service_id").references(
        DeliveryServicesTable.id,
        onDelete = ReferenceOption.NO_ACTION
    )
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object ProductVariantsTable : Table("product_variant") {
    val id = long("id").autoIncrement()
    val productId = long("product_id").references(
        ProductsTable.id,
        onDelete = ReferenceOption.CASCADE
    )
    val manufacturer = varchar("manufacturer", 255).nullable()
    val composition = text("composition").nullable()
    val weight = integer("weight").nullable()

    val calories = integer("calories").nullable()
    val protein = double("protein").nullable()
    val fat = double("fat").nullable()
    val carbs = double("carbs").nullable()

    override val primaryKey = PrimaryKey(id)
}
