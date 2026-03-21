package org.example.api.errors

import io.ktor.http.*
import org.example.api.dto.FieldError

open class ApiException(
    val status: HttpStatusCode,
    val code: String,
    override val message: String,
    val details: List<String>? = null,
    val fieldErrors: List<FieldError>? = null
) : RuntimeException(message)

/** 400 — неверные параметры запроса (формат, тип, диапазон). */
class ValidationException(
    message: String,
    details: List<String>? = null,
    fieldErrors: List<FieldError>? = null
) : ApiException(HttpStatusCode.BadRequest, "VALIDATION_ERROR", message, details, fieldErrors)

/** 422 — запрос корректен по формату, но не прошёл бизнес-валидацию. */
class BusinessValidationException(
    message: String,
    details: List<String>? = null
) : ApiException(HttpStatusCode.UnprocessableEntity, "BUSINESS_VALIDATION_ERROR", message, details, null)

/** 404 — ресурс не найден. */
class NotFoundException(message: String) :
    ApiException(HttpStatusCode.NotFound, "NOT_FOUND", message)

/** 401 — отсутствует или неверный API-ключ. */
class UnauthorizedException(message: String) :
    ApiException(HttpStatusCode.Unauthorized, "UNAUTHORIZED", message)
