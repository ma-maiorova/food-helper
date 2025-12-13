package org.example.repository

import org.example.domain.Product
import org.example.service.model.Page
import org.example.service.model.ProductSearchCriteria

interface ProductRepository {
    suspend fun findById(id: Long): Product?
    suspend fun search(criteria: ProductSearchCriteria): Page<Product>
}
