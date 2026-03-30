# Architecture — Food Helper Backend

## C4 Level 1: System Context

```mermaid
C4Context
    title System Context — Food Helper

    Person(user, "Telegram User", "Ищет продукты по КБЖУ через Telegram-бот")

    System(bot, "Telegram Bot", "Принимает запросы пользователя,\nлистает результаты постранично")
    System(backend, "Food Helper API", "Ktor REST API.\nПоиск продуктов, импорт от парсера")
    System(parser, "Parser", "Обходит сайты доставок,\nобновляет данные раз в сутки")

    System(vkusvill, "ВкусВилл", "Сайт доставки")
    System(lavka, "Яндекс Лавка", "Сайт доставки")
    
    Rel(user, bot, "Отправляет запрос", "Telegram")
    Rel(bot, backend, "GET /api/v1/products", "HTTPS / JSON")
    Rel(parser, backend, "POST /api/v1/admin/import", "HTTPS / JSON")
    Rel(parser, vkusvill, "Парсит", "HTTPS")
    Rel(parser, lavka, "Парсит", "HTTPS")
```

## C4 Level 2: Container

```mermaid
C4Container
    title Container Diagram — Food Helper

    Person(bot, "Telegram Bot")
    Person(parser, "Parser")

    Container(api, "Food Helper API", "Kotlin / Ktor / JVM 17", "REST API: поиск продуктов, импорт")
    ContainerDb(db, "PostgreSQL 16", "PostgreSQL", "Хранит delivery_service, product, product_variant")
    Container(prometheus, "Prometheus", "Prometheus", "Scrape метрик через GET /metrics")

    Rel(bot, api, "GET /api/v1/products\n?q=...&minCalories=...&page=...", "JSON / HTTPS")
    Rel(parser, api, "POST /api/v1/admin/import", "JSON / HTTPS")
    Rel(api, db, "Exposed ORM + HikariCP\n(pool=10)", "JDBC / PostgreSQL")
    Rel(prometheus, api, "GET /metrics", "HTTP / Prometheus text")
```

## C4 Level 3: Component

```mermaid
C4Component
    title Component Diagram — Food Helper API

    Container_Boundary(api, "Food Helper API") {
        Component(routes, "Routes", "Ktor routing\n(ApiRoutes, HealthRoutes, SwaggerRoutes)", "HTTP entry points")
        Component(qparsing, "QueryParsing", "api/validation/QueryParsing.kt", "Парсит и валидирует query params:\nq → AND-токены, sort, page/size, КБЖУ диапазоны")
        Component(productsvc, "ProductService", "service/ProductService.kt", "Поиск; метрики; логирование")
        Component(importsvc, "ProductImportService", "service/ProductImportService.kt", "Валидация батча; чанкирование; upsert")
        Component(ranking, "SearchRanking", "service/SearchRanking.kt", "computeSearchRank: 100/80/50/30")
        Component(productrepo, "ProductRepositoryImpl", "repository/impl/ProductRepositoryImpl.kt", "SQL поиск с AND-семантикой;\nнутриент-фильтр на уровне вариантов;\nранжирование в памяти (до 2000 кандидатов)")
        Component(importrepo, "ProductImportRepositoryImpl", "repository/impl/ProductImportRepositoryImpl.kt", "upsertProduct (по delivery_service_id+url);\nreplaceVariants")
        Component(dsrepo, "DeliveryServiceRepositoryImpl", "repository/impl/DeliveryServiceRepositoryImpl.kt", "CRUD служб доставки")
        Component(metrics, "AppMetrics", "config/MetricsConfig.kt", "Prometheus counters/timers")
        Component(reqid, "RequestId", "config/RequestId.kt", "X-Request-Id в MDC и response")
        Component(statuspage, "StatusPagesConfig", "api/errors/StatusPagesConfig.kt", "ApiException → JSON ErrorResponse")
    }

    ContainerDb(db, "PostgreSQL 16")

    Rel(routes, qparsing, "parseProductSearchCriteria()")
    Rel(routes, productsvc, "search(criteria)")
    Rel(routes, importsvc, "importBatch(request)")
    Rel(productsvc, productrepo, "search(criteria)")
    Rel(productsvc, ranking, "computeSearchRank()")
    Rel(productrepo, ranking, "computeSearchRank() per product")
    Rel(importsvc, importrepo, "upsertProduct(), replaceVariants()")
    Rel(importsvc, dsrepo, "findByCode()")
    Rel(productrepo, db, "Exposed DSL")
    Rel(importrepo, db, "Exposed DSL")
    Rel(dsrepo, db, "Exposed DSL")
    Rel(productsvc, metrics, "increment counters")
    Rel(importsvc, metrics, "increment counters, record timer")
```

---

## ERD

```mermaid
erDiagram
    delivery_service {
        bigserial id PK
        varchar(64) code UK "уникальный код (VKUSVILL, YANDEX_LAVKA)"
        varchar(256) name
        varchar(512) site_url "nullable"
        varchar(512) logo_url "nullable"
        boolean active "default true"
        timestamptz created_at
        timestamptz updated_at
    }

    product {
        bigserial id PK
        varchar(512) name
        varchar(1024) url
        integer price
        varchar(8) currency "default RUB"
        bigint delivery_service_id FK
        timestamptz created_at
        timestamptz updated_at
    }

    product_variant {
        bigserial id PK
        bigint product_id FK
        varchar(255) manufacturer "nullable"
        text composition "nullable"
        integer weight "nullable, > 0"
        integer calories "nullable, >= 0"
        double_precision protein "nullable, >= 0"
        double_precision fat "nullable, >= 0"
        double_precision carbs "nullable, >= 0"
    }

    delivery_service ||--o{ product : "has"
    product ||--o{ product_variant : "has"
```

### Constraints & Indexes

| Object | Type | Definition | Purpose |
|--------|------|-----------|---------|
| `uq_product_delivery_url` | UNIQUE | `(delivery_service_id, url)` | Идемпотентный upsert от парсера |
| `idx_product_delivery_service` | INDEX | `product(delivery_service_id)` | Фильтр по службе доставки |
| `idx_product_name` | INDEX | `product(name)` | Сортировка по имени |
| `idx_product_name_lower_gin` | GIN | `lower(product.name) gin_trgm_ops` | Быстрый substring search по названию (pg_trgm) |
| `idx_variant_composition_lower_gin` | GIN | `lower(composition) gin_trgm_ops` | Быстрый substring search по составу (pg_trgm, CONCURRENT) |
| `idx_variant_product_id` | INDEX | `product_variant(product_id)` | JOIN с product |
| `idx_variant_prod_cal` | INDEX | `product_variant(product_id, calories)` | КБЖУ-фильтр |
| `idx_variant_cal_prot` | INDEX | `product_variant(calories, protein)` | Составной нутриент-фильтр |
| `idx_variant_cal_fat` | INDEX | `product_variant(calories, fat)` | Составной нутриент-фильтр |

---

## Q Search Semantics

```
?q=молоко овсяное
         │
         ▼
tokens = ["молоко", "овсяное"]    (trim → lowercase → split by \s+ → filter empty)
         │
         ▼ SQL WHERE (AND semantics)
for each token:
  product.name ILIKE '%token%'
  OR product.id IN (SELECT product_id FROM product_variant
                     WHERE LOWER(COALESCE(composition,'')) LIKE '%token%')
         │
         ▼ In-memory ranking (up to 2000 candidates)
computeSearchRank(name, matchedVariant.compositions, tokens, fullPhrase)
  → 100: exact phrase in name
  → 80:  all tokens in name
  → 50:  some tokens in name, rest in one composition variant
  → 30:  all tokens only in composition
         │
         ▼ Stable sort: rank DESC, name ASC (case-insensitive), id ASC
         │
         ▼ Page slice: drop(page*size).take(size)
```

## Import Flow

```
POST /api/v1/admin/import
         │
         ▼ Request-level validation (→ 400 if any fails)
  deliveryServiceCode not blank
  items not empty
  items.size ≤ maxItemsPerRequest (default 500)
         │
         ▼ Resolve deliveryService by code (→ error result если не найден)
         │
         ▼ Chunk items by chunkSize (default 100)
         │
  for each item:
    ▼ Item-level validation (→ failed item, rest continue)
      name/url not blank, url absolute http/https, url ≤ 1024, name ≤ 512
      price ≥ 0, currency not blank
      variants not empty
      weight > 0 if provided
      nutrient values ≥ 0 if provided
    ▼ upsertProduct(deliveryServiceId, name, url, price, currency)
        unique on (delivery_service_id, url) — UPDATE or INSERT
    ▼ replaceVariants(productId, variants)
        DELETE all variants for product, bulk INSERT new ones
         │
         ▼ Return ImportResultDto {
             totalReceived, duplicatesResolved,
             created, updated, failed,
             durationMs, errors[]
           }
```

---

## Deployment Architecture (prod)

```
Internet
   │  HTTPS :443 / HTTP :80
   ▼
nginx (Ingress Controller)          ← food_helper_nginx, port 80
   ├── GET /                        → serve /usr/share/nginx/html (Frontend SPA, volume)
   ├── GET /api/*                   → proxy_pass http://food_helper_api:8080
   ├── GET /health, /ready, /swagger
   └── GET /metrics                 → only 127.0.0.1 (deny external)
         │
         ▼ HTTP (internal Docker network: food_helper_net)
Food Helper API                     ← food_helper_api, binds 0.0.0.0:8080 (внутри сети)
   └── JDBC
         ▼
PostgreSQL 16                       ← food_helper_postgres, порт 5432 (internal only)

Prometheus                          ← food_helper_prometheus, 127.0.0.1:9090
   └── scrape food_helper_api:8080/metrics every 15s
         ▼
Grafana                             ← food_helper_grafana, 127.0.0.1:3000 (SSH tunnel)
```

### nginx Key Settings

| Параметр | Значение | Причина |
|----------|----------|---------|
| `client_max_body_size` | `5m` | 500 items × ~2 KB ≈ 1 MB + запас |
| `proxy_read_timeout` | `60s` | Импорт 500 items + запись в БД |
| `proxy_set_header X-Forwarded-Proto` | `$scheme` | Логирование HTTPS |
| `proxy_set_header X-Real-IP` | `$remote_addr` | IP клиента в логах |
| `/metrics` allow | `127.0.0.1` | Метрики не доступны снаружи |

### Container Names & Ports

| Контейнер | Внутренний порт | Локальный | Прод |
|-----------|----------------|-----------|------|
| `food_helper_nginx` | 80 | 8090 | 80 |
| `food_helper_api` | 8080 | — (нет проброса) | — |
| `food_helper_postgres` | 5432 | 5433 | 5432 (127.0.0.1) |
| `food_helper_prometheus` | 9090 | 9091 | 9090 (127.0.0.1) |
| `food_helper_grafana` | 3000 | 3001 | 3000 (SSH tunnel) |

---

## Observability

### Structured Logs

| Event | Поля |
|-------|------|
| `search.start` | q, page, size, filters, requestId |
| `search.finish` | q, totalElements, durationMs, requestId |
| `search.empty_result` | q, filters, requestId |
| `import.start` | deliveryServiceCode, itemsCount, requestId |
| `import.finish` | created, updated, failed, durationMs, requestId |
| `import.item_error` | itemIndex, url, errorCode, message, requestId |

### Prometheus Metrics

| Метрика | Тип | Описание |
|---------|-----|---------|
| `product_search_requests_total` | Counter | Всего поисковых запросов |
| `product_search_empty_results_total` | Counter | Запросов с пустой выдачей |
| `product_import_requests_total` | Counter | Всего запросов импорта |
| `product_import_items_total` | Counter | Всего items в импорте |
| `product_import_failed_items_total` | Counter | Провалившихся items |
| `product_import_duration` | Timer | Длительность импорта |
| HTTP-метрики | auto | Ktor MicrometerMetrics (latency, status codes) |

### Request ID

Каждый запрос получает UUID в `X-Request-Id`:
- Берётся из входящего заголовка `X-Request-Id` (если задан клиентом)
- Иначе генерируется
- Пишется в MDC Logback → присутствует во всех log-строках
- Возвращается в ответе и в теле `ErrorResponse.requestId`
