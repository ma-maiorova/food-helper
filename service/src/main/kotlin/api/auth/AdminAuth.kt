package org.example.api.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.util.pipeline.PipelineContext
import org.example.api.dto.ErrorResponse
import org.example.config.requestId
import org.slf4j.LoggerFactory
import java.time.Instant

private val log = LoggerFactory.getLogger("AdminAuth")

/**
 * Installs X-Api-Key authentication on the receiver [Route].
 * Requests without the header or with a wrong key get 401 Unauthorized.
 * Call this inside the route block that should be protected:
 *
 *   route("/admin") {
 *       installApiKeyAuth(adminApiKey)
 *       // ... admin route handlers
 *   }
 */
fun Route.installApiKeyAuth(expectedKey: String) {
    intercept(io.ktor.server.application.ApplicationCallPipeline.Plugins) {
        if (!call.verifyApiKey(expectedKey)) {
            finish()
        }
    }
}

private suspend fun ApplicationCall.verifyApiKey(expectedKey: String): Boolean {
    val providedKey = request.headers["X-Api-Key"]
    return if (providedKey == null || providedKey != expectedKey) {
        log.warn(
            "auth.failure requestId={} path={} reason={}",
            requestId(),
            request.path(),
            if (providedKey == null) "missing_key" else "invalid_key"
        )
        respond(
            HttpStatusCode.Unauthorized,
            ErrorResponse(
                requestId = requestId(),
                code = "UNAUTHORIZED",
                message = "Требуется заголовок X-Api-Key с корректным ключом",
                status = 401,
                path = request.path(),
                timestamp = Instant.now().toString()
            )
        )
        false
    } else {
        true
    }
}
