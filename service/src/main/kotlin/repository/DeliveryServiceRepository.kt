package org.example.repository

import org.example.domain.DeliveryService

interface DeliveryServiceRepository {
    suspend fun getAll(): List<DeliveryService>
    suspend fun findById(id: Long): DeliveryService?
}
