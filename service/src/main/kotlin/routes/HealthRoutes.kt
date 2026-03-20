package org.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.config.DatabaseFactory
import java.time.Instant

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: String
)

@Serializable
data class ReadyResponse(
    val status: String,
    val timestamp: String,
    val error: String? = null
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

        /**
         * Readiness probe: verifies the database connection is alive.
         * Returns 200 READY when a connection can be obtained from the pool,
         * or 503 NOT_READY when the DB is unreachable.
         */
        get("/ready") {
            try {
                DatabaseFactory.dataSource.connection.use { conn ->
                    conn.isValid(2) // 2-second timeout
                }
                call.respond(
                    ReadyResponse(
                        status = "READY",
                        timestamp = Instant.now().toString()
                    )
                )
            } catch (e: Exception) {
                call.application.environment.log.warn("Readiness check failed: {}", e.message)
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    ReadyResponse(
                        status = "NOT_READY",
                        timestamp = Instant.now().toString(),
                        error = e.message
                    )
                )
            }
        }
    }
}
