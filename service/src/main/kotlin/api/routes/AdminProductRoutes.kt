package org.example.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.api.dto.UpdateProductRequest
import org.example.api.errors.NotFoundException
import org.example.api.errors.ValidationException
import org.example.api.mapper.toDto
import org.example.service.ProductService

fun Route.adminProductRoutes(service: ProductService) {

    // GET /ping — проверка ключа (всегда 200 если ключ верный, 401 если нет)
    get("/ping") {
        call.respond(mapOf("ok" to true))
    }

    route("/products") {
        // PATCH /products/{id} — частичное обновление продукта и его первого варианта
        patch("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: throw ValidationException("Параметр id должен быть числом")
            val body = call.receive<UpdateProductRequest>()
            val updated = service.update(id, body)
                ?: throw NotFoundException("Продукт #$id не найден")
            call.respond(updated.toDto())
        }

        // DELETE /products/{id} — удаление продукта
        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: throw ValidationException("Параметр id должен быть числом")
            val deleted = service.delete(id)
            if (!deleted) throw NotFoundException("Продукт #$id не найден")
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
