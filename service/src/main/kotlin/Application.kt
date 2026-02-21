package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import kotlinx.serialization.json.Json
import org.example.api.errors.installApiStatusPages
import org.example.api.routes.configureApiRoutes
import org.example.config.DatabaseFactory
import org.example.config.installRequestId
import org.example.config.requestId
import org.example.repository.impl.DeliveryServiceRepositoryImpl
import org.example.repository.impl.ProductRepositoryImpl
import org.example.routes.healthRoutes
import org.example.routes.swaggerRoutes
import org.example.service.DeliveryServiceService
import org.example.service.ProductService
import org.slf4j.event.Level

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment)

    installRequestId()
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val requestId = call.requestId()
            val status = call.response.status()
            "requestId=$requestId ${call.request.httpMethod.value} ${call.request.path()} -> $status"
        }
    }

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
        )
    }

    installApiStatusPages()
    val deliveryServiceService = DeliveryServiceService(DeliveryServiceRepositoryImpl)
    val productService = ProductService(ProductRepositoryImpl)

    healthRoutes()
    swaggerRoutes()
    configureApiRoutes(deliveryServiceService, productService)
}
