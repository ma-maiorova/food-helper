package org.example.api.errors

import io.ktor.http.*

open class ApiException(
    val status: HttpStatusCode,
    val code: String,
    override val message: String
) : RuntimeException(message)

class ValidationException(message: String) :
    ApiException(HttpStatusCode.BadRequest, "VALIDATION_ERROR", message)

class NotFoundException(message: String) :
    ApiException(HttpStatusCode.NotFound, "NOT_FOUND", message)
