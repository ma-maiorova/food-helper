package org.example.api.errors

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.path
import io.ktor.server.response.*
import org.example.api.dto.ErrorResponse
import org.example.config.requestId
import java.time.Instant

fun Application.installApiStatusPages() {
    install(StatusPages) {

        exception<ApiException> { call, cause ->
            call.respond(
                cause.status,
                ErrorResponse(
                    requestId = call.requestId(),
                    code = cause.code,
                    message = cause.message,
                    status = cause.status.value,
                    path = call.request.path(),
                    timestamp = Instant.now().toString(),
                    details = cause.details,
                    fieldErrors = cause.fieldErrors
                )
            )
        }

        // Ktor throws BadRequestException for malformed JSON / deserialization failures
        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    requestId = call.requestId(),
                    code = "BAD_REQUEST",
                    message = cause.message ?: "Bad request",
                    status = 400,
                    path = call.request.path(),
                    timestamp = Instant.now().toString()
                )
            )
        }

        exception<Throwable> { call, cause ->
            val requestId = call.requestId()
            call.application.environment.log.error("requestId=$requestId path=${call.request.path()} error=${cause.message}", cause)

            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    requestId = call.requestId(),
                    code = "INTERNAL_ERROR",
                    message = "Внутренняя ошибка сервера",
                    status = 500,
                    path = call.request.path(),
                    timestamp = Instant.now().toString()
                )
            )
        }
    }
}
