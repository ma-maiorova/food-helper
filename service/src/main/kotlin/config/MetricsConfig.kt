package org.example.config

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.Timer
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

/**
 * Application-wide Micrometer metrics registry (Prometheus format).
 * Exposed at GET /metrics.
 *
 * HTTP request metrics (http_server_requests_seconds_*) are added automatically by the
 * Ktor MicrometerMetrics plugin installed in Application.module().
 */
object AppMetrics {
    val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    // ── Search metrics ────────────────────────────────────────────────────────

    val searchEmptyResultsTotal: Counter = Counter.builder("product_search_empty_results_total")
        .description("Search requests that returned zero results")
        .register(registry)

    /** Labeled search counter. [hasQuery] = "true" if q param was provided. [source] = "bot" or "web". */
    fun searchRequestsTotal(hasQuery: Boolean, source: String = "web"): Counter =
        registry.counter(
            "product_search_requests_total",
            "has_query", hasQuery.toString(),
            "source", source
        )

    // ── Import metrics — labeled by delivery_service ──────────────────────────

    /** Total import batch requests per delivery service. */
    fun importRequestsTotal(deliveryService: String): Counter =
        registry.counter("product_import_requests_total", "delivery_service", deliveryService)

    /** Total items submitted (before dedup) per delivery service. */
    fun importItemsTotal(deliveryService: String): Counter =
        registry.counter("product_import_items_total", "delivery_service", deliveryService)

    /** Items successfully created (new product) per delivery service. */
    fun importCreatedItemsTotal(deliveryService: String): Counter =
        registry.counter("product_import_created_items_total", "delivery_service", deliveryService)

    /** Items successfully updated (existing product) per delivery service. */
    fun importUpdatedItemsTotal(deliveryService: String): Counter =
        registry.counter("product_import_updated_items_total", "delivery_service", deliveryService)

    /** Failed items per delivery service and error code. */
    fun importFailedItemsTotal(deliveryService: String, errorCode: String): Counter =
        registry.counter(
            "product_import_failed_items_total",
            "delivery_service", deliveryService,
            "error_code", errorCode
        )

    /** Items de-duplicated within a single batch (last wins) per delivery service. */
    fun importDuplicateOverriddenTotal(deliveryService: String): Counter =
        registry.counter("product_import_duplicate_overridden_total", "delivery_service", deliveryService)

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
}
