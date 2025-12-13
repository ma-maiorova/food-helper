package org.example.service

import org.example.domain.Product
import org.example.repository.ProductRepository
import org.example.service.model.Page
import org.example.service.model.ProductSearchCriteria

class ProductService(
    private val repo: ProductRepository
) {
    suspend fun search(criteria: ProductSearchCriteria): Page<Product> =
        repo.search(criteria)
}
