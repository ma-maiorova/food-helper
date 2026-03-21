package org.example.service

import org.example.config.AppMetrics
import org.example.domain.Product
import org.example.repository.ProductRepository
import org.example.service.model.Page
import org.example.service.model.ProductSearchCriteria
import org.slf4j.LoggerFactory

class ProductService(
    private val repo: ProductRepository
) {
    private val log = LoggerFactory.getLogger(ProductService::class.java)

    suspend fun search(criteria: ProductSearchCriteria): Page<Product> {
        log.info(
            "search.start query={} deliveryServiceIds={} page={} size={} sort={}",
            criteria.query, criteria.deliveryServiceIds, criteria.page, criteria.size, criteria.sort
        )

        AppMetrics.searchRequestsTotal(hasQuery = criteria.query != null).increment()

        val result = repo.search(criteria)

        if (result.totalElements == 0L) {
            log.info("search.empty_result query={}", criteria.query)
            AppMetrics.searchEmptyResultsTotal.increment()
        } else {
            log.info(
                "search.finish query={} totalElements={} returnedItems={}",
                criteria.query, result.totalElements, result.items.size
            )
        }

        return result
    }
}
