package org.example.api.dto

import kotlinx.serialization.Serializable

/**
 * Стабильные machine-readable коды ошибок для failed items при импорте.
 * Используются в [ImportErrorItemDto.errorCode] и метриках.
 */
object ImportErrorCode {
    const val BLANK_NAME = "blank_name"
    const val BLANK_URL = "blank_url"
    const val INVALID_URL = "invalid_url"
    const val URL_TOO_LONG = "url_too_long"
    const val NAME_TOO_LONG = "name_too_long"
    const val NEGATIVE_PRICE = "negative_price"
    const val BLANK_CURRENCY = "blank_currency"
    const val INVALID_WEIGHT = "invalid_weight"
    const val NEGATIVE_NUTRIENTS = "negative_nutrients"
    const val EMPTY_VARIANTS = "empty_variants"
    const val UNKNOWN_SERVICE = "unknown_delivery_service"
    const val INTERNAL_ERROR = "internal_error"
}

/**
 * Результат импорта батча продуктов.
 *
 * - [totalReceived] — количество элементов в исходном запросе (до дедупликации по URL)
 * - [duplicatesResolved] — количество элементов, отброшенных как дубли URL внутри батча (last wins)
 * - [created] — новые продукты, добавленные в БД
 * - [updated] — существующие продукты, обновлённые в БД
 * - [failed] — элементы, не импортированные из-за ошибок валидации или БД
 * - [durationMs] — время обработки батча в миллисекундах
 * - [errors] — список ошибок по элементам с machine-readable кодом
 */
@Serializable
data class ImportResultDto(
    val totalReceived: Int,
    val duplicatesResolved: Int,
    val created: Int,
    val updated: Int,
    val failed: Int,
    val durationMs: Long,
    val errors: List<ImportErrorItemDto> = emptyList()
)

@Serializable
data class ImportErrorItemDto(
    val itemIndex: Int,
    val url: String? = null,
    val name: String? = null,
    /** Machine-readable код ошибки. Стабилен между версиями. */
    val errorCode: String,
    val message: String
)
