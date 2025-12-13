package org.example.service

import org.example.domain.DeliveryService
import org.example.repository.DeliveryServiceRepository

class DeliveryServiceService(
    private val repo: DeliveryServiceRepository
) {
    suspend fun list(activeOnly: Boolean? = null): List<DeliveryService> {
        val all = repo.getAll()
        return when (activeOnly) {
            true -> all.filter { it.active }
            false -> all
            null -> all
        }
    }
}
