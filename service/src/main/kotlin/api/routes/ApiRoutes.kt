package org.example.api.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.example.api.auth.installApiKeyAuth
import org.example.service.DeliveryServiceService
import org.example.service.ProductImportService
import org.example.service.ProductService

fun Application.configureApiRoutes(
    deliveryServiceService: DeliveryServiceService,
    productService: ProductService,
    productImportService: ProductImportService,
    adminApiKey: String
) {
    routing {
        route("/api/v1") {
            deliveryServiceRoutes(deliveryServiceService)
            productRoutes(productService)
            route("/admin") {
                installApiKeyAuth(adminApiKey)
                adminDeliveryServiceRoutes(deliveryServiceService)
                adminImportRoutes(productImportService)
            }
        }
    }
}
