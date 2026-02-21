package org.example.config

import io.ktor.server.application.*
import io.ktor.server.request.header
import io.ktor.util.*
import org.slf4j.MDC
import java.util.UUID

val RequestIdKey = AttributeKey<String>("RequestId")

/**
 * Устанавливает requestId в начале запроса (из заголовка X-Request-Id или новый UUID),
 * кладёт в [call.attributes] и в MDC для логов. Очищает MDC после обработки.
 */
fun Application.installRequestId() {
    intercept(ApplicationCallPipeline.Setup) {
        val id = call.request.header("X-Request-Id")?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()
        call.attributes.put(RequestIdKey, id)
        MDC.put("requestId", id)
    }
    intercept(ApplicationCallPipeline.Call) {
        try {
            proceed()
        } finally {
            MDC.remove("requestId")
        }
    }
}

fun ApplicationCall.requestId(): String =
    attributes.getOrNull(RequestIdKey)
        ?: request.header("X-Request-Id")?.takeIf { it.isNotBlank() }
        ?: UUID.randomUUID().toString()
