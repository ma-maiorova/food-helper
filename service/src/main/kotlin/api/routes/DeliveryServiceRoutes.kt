package org.example.api.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.api.mapper.toDto
import org.example.service.DeliveryServiceService

fun Route.deliveryServiceRoutes(service: DeliveryServiceService) {
    route("/delivery-services") {
        get {
            val activeOnly = call.request.queryParameters["active"]
                ?.lowercase()
                ?.let { it == "true" || it == "1" }

            val items = service.list(activeOnly).map { it.toDto() }
            call.respond(items)
        }
    }
}
