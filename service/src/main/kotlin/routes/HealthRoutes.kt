package org.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: String
)

fun Application.healthRoutes() {
    routing {
        get("/health") {
            call.respond(
                HealthResponse(
                    status = "OK",
                    timestamp = Instant.now().toString()
                )
            )
        }
    }
}
