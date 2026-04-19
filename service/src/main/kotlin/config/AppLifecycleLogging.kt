package org.example.config

import io.ktor.server.application.*
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("org.example.lifecycle")

/**
 * Логирование старта и остановки сервера.
 * По строкам "Server started" / "Server stopped" в логах удобно проверять, что сервис жив и корректно завершился.
 * Не логируем конфиг с паролями и другими чувствительными данными.
 */
fun Application.installLifecycleLogging() {
    environment.monitor.subscribe(ApplicationStarted) {
        val port = try {
            environment.config.property("ktor.deployment.port").getString()
        } catch (_: Exception) {
            "8080"
        }
        log.info("Server started; port={} ready to accept connections", port)
    }
    environment.monitor.subscribe(ApplicationStopped) {
        log.info("Server stopped; shutting down")
    }
}
