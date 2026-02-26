package org.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*

fun Application.swaggerRoutes() {
    routing {
        staticResources("/openapi", "openapi")
        staticResources("/swagger", "swagger-ui")

        get("/swagger") {
            call.respondRedirect("/swagger/index.html", permanent = false)
        }
    }
}
