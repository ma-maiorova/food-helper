package org.example.repository

import org.example.api.dto.UpdateProductRequest
import org.example.domain.Product
import org.example.service.model.Page
import org.example.service.model.ProductSearchCriteria

interface ProductRepository {
    suspend fun findById(id: Long): Product?
    suspend fun search(criteria: ProductSearchCriteria): Page<Product>
    suspend fun existsByDeliveryServiceId(deliveryServiceId: Long): Boolean
    suspend fun deleteById(id: Long): Boolean
    suspend fun updateProduct(id: Long, req: UpdateProductRequest): Product?
}
