package org.example.api.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.api.dto.ParserImportRequest
import org.example.service.ProductImportService

fun Route.adminImportRoutes(importService: ProductImportService) {
    route("/import") {
        post {
            val body = call.receive<ParserImportRequest>()
            val result = importService.importBatch(body)
            call.respond(result)
        }
    }
}
