package org.example.api.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.example.service.DeliveryServiceService
import org.example.service.ProductService

fun Application.configureApiRoutes(
    deliveryServiceService: DeliveryServiceService,
    productService: ProductService
) {
    routing {
        route("/api/v1") {
            deliveryServiceRoutes(deliveryServiceService)
            productRoutes(productService)
        }
    }
}
