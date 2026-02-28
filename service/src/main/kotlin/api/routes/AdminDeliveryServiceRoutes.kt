package org.example.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.api.dto.CreateDeliveryServiceRequest
import org.example.api.dto.UpdateDeliveryServiceRequest
import org.example.api.errors.ValidationException
import org.example.api.mapper.toDto
import org.example.service.DeliveryServiceService

fun Route.adminDeliveryServiceRoutes(service: DeliveryServiceService) {
    route("/admin/delivery-services") {
        post {
            val body = call.receive<CreateDeliveryServiceRequest>()
            val created = service.create(
                code = body.code,
                name = body.name,
                siteUrl = body.siteUrl,
                logoUrl = body.logoUrl,
                active = body.active
            )
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: throw ValidationException("Параметр id должен быть числом")
            val body = call.receive<UpdateDeliveryServiceRequest>()
            val updated = service.update(
                id = id,
                name = body.name,
                siteUrl = body.siteUrl,
                logoUrl = body.logoUrl,
                active = body.active
            )
            call.respond(updated.toDto())
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: throw ValidationException("Параметр id должен быть числом")
            service.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
