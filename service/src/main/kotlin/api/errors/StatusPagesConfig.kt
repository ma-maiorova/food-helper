package org.example.api.errors

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.path
import io.ktor.server.response.*
import org.example.api.dto.ErrorResponse
import java.time.Instant

fun Application.installApiStatusPages() {
    install(StatusPages) {

        exception<ApiException> { call, cause ->
            call.respond(
                cause.status,
                ErrorResponse(
                    timestamp = Instant.now().toString(),
                    status = cause.status.value,
                    error = cause.status.description,
                    code = cause.code,
                    message = cause.message,
                    path = call.request.path()
                )
            )
        }

        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled error", cause)

            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    timestamp = Instant.now().toString(),
                    status = 500,
                    error = "Internal Server Error",
                    code = "INTERNAL_ERROR",
                    message = "Unexpected error",
                    path = call.request.path()
                )
            )
        }
    }
}
