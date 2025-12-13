package org.example.repository.impl

import org.example.config.DatabaseFactory
import org.example.domain.DeliveryService
import org.example.repository.DeliveryServiceRepository
import org.example.repository.DeliveryServicesTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

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
                .select { DeliveryServicesTable.id eq id }
                .limit(1)
                .firstOrNull()
                ?.toDomain()
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
