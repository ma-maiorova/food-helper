package org.example.api.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.api.dto.PageResponse
import org.example.api.mapper.toDto
import org.example.api.validation.parseProductSearchCriteria
import org.example.service.ProductService

fun Route.productRoutes(service: ProductService) {
    route("/products") {
        get {
            val criteria = parseProductSearchCriteria(call)
            val page = service.search(criteria)

            call.respond(
                PageResponse(
                    items = page.items.map { it.toDto() },
                    page = page.page,
                    size = page.size,
                    totalElements = page.totalElements,
                    totalPages = page.totalPages
                )
            )
        }
    }
}
