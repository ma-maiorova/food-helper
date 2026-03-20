package org.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.example.api.errors.installApiStatusPages
import org.example.api.routes.configureApiRoutes
import org.example.config.AppMetrics
import org.example.config.DatabaseFactory
import org.example.config.installLifecycleLogging
import org.example.config.installRequestId
import org.example.config.requestId
import org.example.repository.impl.DeliveryServiceRepositoryImpl
import org.example.repository.impl.ProductImportRepositoryImpl
import org.example.repository.impl.ProductRepositoryImpl
import org.example.routes.healthRoutes
import org.example.routes.swaggerRoutes
import org.example.service.DeliveryServiceService
import org.example.service.ProductImportService
import org.example.service.ProductService
import org.slf4j.event.Level

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment)
    installLifecycleLogging()

    installRequestId()
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val requestId = call.requestId()
            val status = call.response.status()
            "requestId=$requestId method=${call.request.httpMethod.value} path=${call.request.path()} status=${status?.value}"
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

    install(MicrometerMetrics) {
        registry = AppMetrics.registry
    }

    installApiStatusPages()

    val maxItemsPerRequest = environment.config.propertyOrNull("ktor.import.maxItemsPerRequest")?.getString()?.toIntOrNull() ?: 500
    val importChunkSize = environment.config.propertyOrNull("ktor.import.chunkSize")?.getString()?.toIntOrNull() ?: 100
    val deliveryServiceService = DeliveryServiceService(DeliveryServiceRepositoryImpl, ProductRepositoryImpl)
    val productService = ProductService(ProductRepositoryImpl)
    val productImportService = ProductImportService(DeliveryServiceRepositoryImpl, ProductImportRepositoryImpl, maxItemsPerRequest, importChunkSize)

    healthRoutes()
    swaggerRoutes()
    configureApiRoutes(deliveryServiceService, productService, productImportService)

    routing {
        get("/metrics") {
            call.respondText(
                AppMetrics.registry.scrape(),
                contentType = ContentType.Text.Plain
            )
        }
    }
}
