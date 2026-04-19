package org.example.repository

import org.example.domain.DeliveryService

interface DeliveryServiceRepository {
    suspend fun getAll(): List<DeliveryService>
    suspend fun findById(id: Long): DeliveryService?
    suspend fun findByCode(code: String): DeliveryService?
    suspend fun create(code: String, name: String, siteUrl: String?, logoUrl: String?, active: Boolean): DeliveryService
    suspend fun update(id: Long, name: String?, siteUrl: String?, logoUrl: String?, active: Boolean?): DeliveryService?
    suspend fun delete(id: Long): Boolean
}
