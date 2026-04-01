package org.example.service

import org.example.api.errors.BusinessValidationException
import org.example.api.errors.NotFoundException
import org.example.domain.DeliveryService
import org.example.repository.DeliveryServiceRepository
import org.example.repository.ProductRepository

class DeliveryServiceService(
    private val repo: DeliveryServiceRepository,
    private val productRepository: ProductRepository
) {
    suspend fun list(activeOnly: Boolean? = null): List<DeliveryService> {
        val all = repo.getAll()
        return when (activeOnly) {
            true -> all.filter { it.active }
            false -> all
            null -> all
        }
    }

    suspend fun create(
        code: String,
        name: String,
        siteUrl: String?,
        logoUrl: String?,
        active: Boolean
    ): DeliveryService {
        val codeTrimmed = code.trim()
        val nameTrimmed = name.trim()
        if (codeTrimmed.isEmpty()) {
            throw BusinessValidationException("Код службы доставки не может быть пустым")
        }
        if (nameTrimmed.isEmpty()) {
            throw BusinessValidationException("Название службы доставки не может быть пустым")
        }
        if (repo.findByCode(codeTrimmed) != null) {
            throw BusinessValidationException("Служба доставки с кодом '$codeTrimmed' уже существует")
        }
        return repo.create(codeTrimmed, nameTrimmed, siteUrl?.trim()?.takeIf { it.isNotEmpty() }, logoUrl?.trim()?.takeIf { it.isNotEmpty() }, active)
    }

    suspend fun update(
        id: Long,
        name: String?,
        siteUrl: String?,
        logoUrl: String?,
        active: Boolean?
    ): DeliveryService {
        if (repo.findById(id) == null) {
            throw NotFoundException("Служба доставки с id=$id не найдена")
        }
        val updated = repo.update(
            id,
            name?.trim()?.takeIf { it.isNotEmpty() },
            siteUrl?.trim()?.takeIf { it.isNotEmpty() },
            logoUrl?.trim()?.takeIf { it.isNotEmpty() },
            active
        )
        return updated ?: throw NotFoundException("Служба доставки с id=$id не найдена")
    }

    suspend fun delete(id: Long) {
        if (repo.findById(id) == null) {
            throw NotFoundException("Служба доставки с id=$id не найдена")
        }
        if (productRepository.existsByDeliveryServiceId(id)) {
            throw BusinessValidationException("Невозможно удалить службу доставки: есть связанные продукты")
        }
        if (!repo.delete(id)) {
            throw NotFoundException("Служба доставки с id=$id не найдена")
        }
    }
}
