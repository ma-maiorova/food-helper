package org.example.config

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.Timer
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.example.api.dto.ImportErrorCode

/**
 * Application-wide Micrometer metrics registry (Prometheus format).
 * Exposed at GET /metrics.
 *
 * HTTP request metrics (http_server_requests_seconds_*) are added automatically by the
 * Ktor MicrometerMetrics plugin installed in Application.module().
 *
 * Import metrics are pre-registered at startup for all known delivery services so they
 * appear in Prometheus scrapes immediately (before the first import request).
 */
object AppMetrics {
    val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    // ── Known delivery service codes (must match DB) ──────────────────────────
    // Pre-registered so Prometheus sees them from the first scrape, not only after
    // the first import request. New services added to the DB will still be registered
    // lazily on first use.
    private val knownDeliveryServices = listOf("VKUSVILL", "YANDEX_LAVKA", "YANDEX_EATS")

    // ── Search metrics ────────────────────────────────────────────────────────

    val searchEmptyResultsTotal: Counter = Counter.builder("product_search_empty_results_total")
        .description("Search requests that returned zero results")
        .register(registry)

    /** Labeled search counter. [hasQuery] = "true" if q param was provided. */
    fun searchRequestsTotal(hasQuery: Boolean): Counter =
        registry.counter("product_search_requests_total", "has_query", hasQuery.toString())

    // ── Import metrics — labeled by delivery_service ──────────────────────────

    /** Total import batch requests per delivery service. */
    fun importRequestsTotal(deliveryService: String): Counter =
        Counter.builder("product_import_requests_total")
            .description("Total import batch requests per delivery service")
            .tag("delivery_service", deliveryService)
            .register(registry)

    /** Total items submitted (before dedup) per delivery service. */
    fun importItemsTotal(deliveryService: String): Counter =
        Counter.builder("product_import_items_total")
            .description("Total items submitted per delivery service")
            .tag("delivery_service", deliveryService)
            .register(registry)

    /** Items successfully created (new product) per delivery service. */
    fun importCreatedItemsTotal(deliveryService: String): Counter =
        Counter.builder("product_import_created_items_total")
            .description("Items created (new product) per delivery service")
            .tag("delivery_service", deliveryService)
            .register(registry)

    /** Items successfully updated (existing product) per delivery service. */
    fun importUpdatedItemsTotal(deliveryService: String): Counter =
        Counter.builder("product_import_updated_items_total")
            .description("Items updated (existing product) per delivery service")
            .tag("delivery_service", deliveryService)
            .register(registry)

    /** Failed items per delivery service and error code. */
    fun importFailedItemsTotal(deliveryService: String, errorCode: String): Counter =
        Counter.builder("product_import_failed_items_total")
            .description("Failed items per delivery service and error code")
            .tag("delivery_service", deliveryService)
            .tag("error_code", errorCode)
            .register(registry)

    /** Items de-duplicated within a single batch (last wins) per delivery service. */
    fun importDuplicateOverriddenTotal(deliveryService: String): Counter =
        Counter.builder("product_import_duplicate_overridden_total")
            .description("Items de-duplicated within batch per delivery service")
            .tag("delivery_service", deliveryService)
            .register(registry)

    /** Import batch processing duration (seconds) per delivery service. */
    fun importDuration(deliveryService: String): Timer =
        Timer.builder("product_import_duration_seconds")
            .description("Duration of import batch processing")
            .tag("delivery_service", deliveryService)
            .publishPercentiles(0.5, 0.9, 0.95)
            .register(registry)

    /** Distribution of import batch sizes (item count per request). */
    val importBatchSize: DistributionSummary = DistributionSummary.builder("product_import_batch_size")
        .description("Distribution of import batch sizes (items per request)")
        .publishPercentiles(0.5, 0.75, 0.9, 0.95)
        .register(registry)

    /**
     * Pre-registers all import counters for known delivery services and all known
     * error codes so Prometheus sees them from the very first scrape.
     * Called once from Application.module() after the registry is set up.
     */
    fun preRegisterImportMetrics() {
        // Pre-register search labels
        searchRequestsTotal(true)
        searchRequestsTotal(false)

        val allErrorCodes = listOf(
            ImportErrorCode.BLANK_NAME,
            ImportErrorCode.BLANK_URL,
            ImportErrorCode.INVALID_URL,
            ImportErrorCode.URL_TOO_LONG,
            ImportErrorCode.NAME_TOO_LONG,
            ImportErrorCode.NEGATIVE_PRICE,
            ImportErrorCode.BLANK_CURRENCY,
            ImportErrorCode.INVALID_WEIGHT,
            ImportErrorCode.NEGATIVE_NUTRIENTS,
            ImportErrorCode.EMPTY_VARIANTS,
            ImportErrorCode.UNKNOWN_SERVICE,
            ImportErrorCode.INTERNAL_ERROR,
        )

        knownDeliveryServices.forEach { svc ->
            importRequestsTotal(svc)
            importItemsTotal(svc)
            importCreatedItemsTotal(svc)
            importUpdatedItemsTotal(svc)
            importDuplicateOverriddenTotal(svc)
            importDuration(svc)
            allErrorCodes.forEach { code -> importFailedItemsTotal(svc, code) }
        }
    }
}
