package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.api.errors.installApiStatusPages
import org.example.api.routes.configureApiRoutes
import org.example.config.DatabaseFactory
import org.example.repository.impl.DeliveryServiceRepositoryImpl
import org.example.repository.impl.ProductRepositoryImpl
import org.example.routes.healthRoutes
import org.example.service.DeliveryServiceService
import org.example.service.ProductService
import org.slf4j.event.Level

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment)

    install(CallLogging) {
        level = Level.INFO
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
    configureApiRoutes(deliveryServiceService, productService)

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            this@module.environment.log.error("Unhandled error", cause)
            call.respondText(
                "Internal server error",
                status = io.ktor.http.HttpStatusCode.InternalServerError
            )
        }
    }

    healthRoutes()
}
