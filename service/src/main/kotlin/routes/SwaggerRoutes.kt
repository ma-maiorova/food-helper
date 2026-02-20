package org.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*

fun Application.swaggerRoutes() {
    routing {
        route("/openapi") {
            staticResources("", "openapi")
        }

        route("/swagger-ui") {
            staticResources("", "META-INF/resources/webjars/swagger-ui/5.10.3")
        }

        get("/swagger") {
            call.respondRedirect("/swagger-ui/index.html?url=/openapi/openapi.yaml", permanent = false)
        }
    }
}
