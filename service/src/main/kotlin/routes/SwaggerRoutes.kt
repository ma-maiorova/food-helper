package org.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*

fun Application.swaggerRoutes() {
    routing {
        staticResources("/openapi", "openapi")

        get("/swagger") {
            call.respondRedirect("/swagger-ui/index.html", permanent = false)
        }
        get("/swagger-ui") {
            call.respondRedirect("/swagger-ui/index.html", permanent = false)
        }
        get("/swagger-ui/") {
            call.respondRedirect("/swagger-ui/index.html", permanent = false)
        }
        get("/swagger-ui/index.html") {
            val stream = call.application.javaClass.classLoader.getResourceAsStream("swagger-ui/index.html")
                ?: return@get call.respond(HttpStatusCode.NotFound)
            val bytes = stream.readBytes()
            call.respondBytes(bytes, ContentType.Text.Html)
        }
    }
}
