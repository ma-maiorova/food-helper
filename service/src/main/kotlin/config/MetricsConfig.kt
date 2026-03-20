package org.example.config

import io.micrometer.core.instrument.Counter
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

    // Search metrics
    val searchRequestsTotal: Counter = Counter.builder("product_search_requests_total")
        .description("Total product search requests")
        .register(registry)

    val searchEmptyResultsTotal: Counter = Counter.builder("product_search_empty_results_total")
        .description("Search requests that returned zero results")
        .register(registry)

    // Import metrics
    val importRequestsTotal: Counter = Counter.builder("product_import_requests_total")
        .description("Total import batch requests")
        .register(registry)

    val importItemsTotal: Counter = Counter.builder("product_import_items_total")
        .description("Total items submitted for import")
        .register(registry)

    val importFailedItemsTotal: Counter = Counter.builder("product_import_failed_items_total")
        .description("Total failed items during import")
        .register(registry)

    val importDuration: Timer = Timer.builder("product_import_duration")
        .description("Duration of import batch processing")
        .register(registry)
}
