package org.example.repository.impl

import org.example.config.DatabaseFactory
import org.example.domain.DeliveryService
import org.example.repository.DeliveryServiceRepository
import org.example.repository.DeliveryServicesTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

object DeliveryServiceRepositoryImpl : DeliveryServiceRepository {

    override suspend fun getAll(): List<DeliveryService> =
        DatabaseFactory.dbQuery {
            DeliveryServicesTable
                .selectAll()
                .map { it.toDomain() }
        }

    override suspend fun findById(id: Long): DeliveryService? =
        DatabaseFactory.dbQuery {
            DeliveryServicesTable
                .selectAll()
                .where { DeliveryServicesTable.id eq id }
                .limit(1)
                .firstOrNull()
                ?.toDomain()
        }

    override suspend fun findByCode(code: String): DeliveryService? =
        DatabaseFactory.dbQuery {
            DeliveryServicesTable
                .selectAll()
                .where { DeliveryServicesTable.code eq code }
                .limit(1)
                .firstOrNull()
                ?.toDomain()
        }

    override suspend fun create(
        code: String,
        name: String,
        siteUrl: String?,
        logoUrl: String?,
        active: Boolean
    ): DeliveryService = DatabaseFactory.dbQuery {
        val now = Instant.now()
        DeliveryServicesTable.insert {
            it[DeliveryServicesTable.code] = code
            it[DeliveryServicesTable.name] = name
            it[DeliveryServicesTable.siteUrl] = siteUrl
            it[DeliveryServicesTable.logoUrl] = logoUrl
            it[DeliveryServicesTable.active] = active
            it[DeliveryServicesTable.createdAt] = now
            it[DeliveryServicesTable.updatedAt] = now
        }
        DeliveryServicesTable.selectAll()
            .where { DeliveryServicesTable.code eq code }
            .limit(1)
            .first()
            .toDomain()
    }

    override suspend fun update(
        id: Long,
        name: String?,
        siteUrl: String?,
        logoUrl: String?,
        active: Boolean?
    ): DeliveryService? = DatabaseFactory.dbQuery {
        val now = Instant.now()
        DeliveryServicesTable.update({ DeliveryServicesTable.id eq id }) {
            name?.let { n -> it[DeliveryServicesTable.name] = n }
            siteUrl?.let { s -> it[DeliveryServicesTable.siteUrl] = s }
            logoUrl?.let { l -> it[DeliveryServicesTable.logoUrl] = l }
            active?.let { a -> it[DeliveryServicesTable.active] = a }
            it[DeliveryServicesTable.updatedAt] = now
        }
        findById(id)
    }

    override suspend fun delete(id: Long): Boolean = DatabaseFactory.dbQuery {
        DeliveryServicesTable.deleteWhere { DeliveryServicesTable.id eq id } > 0
    }

    private fun ResultRow.toDomain(): DeliveryService =
        DeliveryService(
            id = this[DeliveryServicesTable.id],
            code = this[DeliveryServicesTable.code],
            name = this[DeliveryServicesTable.name],
            siteUrl = this[DeliveryServicesTable.siteUrl],
            logoUrl = this[DeliveryServicesTable.logoUrl],
            active = this[DeliveryServicesTable.active]
        )
}
