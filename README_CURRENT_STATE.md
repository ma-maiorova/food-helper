# Food Helper — текущее состояние проекта

Документ фиксирует, что **сейчас реализовано**, какие **ручки доступны** и **как запускать** (по состоянию на момент обновления).

---

## Что есть в проекте

- **Backend (API)** — один сервис в каталоге `service/`: Kotlin, Ktor 2.3, Netty, PostgreSQL (Exposed + Liquibase).
- **Документация** — OpenAPI 3.0 (YAML), Swagger UI, описание DTO и ошибок в `docs/`.
- **Инфраструктура** — Docker Compose для dev (postgres + api), healthchecks, конфиг через `.env`.
- **Telegram-бот и загрузчик данных** — пока не реализованы (запланированы).

---

## Реализованные HTTP-ручки

Все ответы — JSON. Базовый путь API: `/api/v1`. Ошибки возвращаются в едином формате `ErrorResponse` (см. `docs/DTO_SCHEMA.md`).

| Метод | Путь | Назначение |
|-------|------|------------|
| **GET** | `/health` | Проверка живости сервиса (для healthcheck). Ответ: `{ "status": "OK", "timestamp": "..." }`. |
| **GET** | `/swagger` | Редирект на Swagger UI (статика по `/swagger/`). |
| **GET** | `/openapi/openapi.yaml` | Спецификация OpenAPI в YAML. |
| **GET** | `/api/v1/delivery-services` | Список служб доставки. |
| **GET** | `/api/v1/products` | Поиск продуктов с пагинацией и фильтрами по КБЖУ и службам. |
| **POST** | `/api/v1/admin/delivery-services` | Создать службу доставки (админ). |
| **PUT** | `/api/v1/admin/delivery-services/{id}` | Обновить службу доставки (админ). |
| **DELETE** | `/api/v1/admin/delivery-services/{id}` | Удалить службу доставки (админ). |

### GET /api/v1/delivery-services

- **Параметры:**  
  - `active` (query, необязательный) — фильтр по активности: `true`/`false` или `1`/`0`.
- **Ответ:** массив объектов служб доставки (id, code, name, siteUrl, logoUrl, active).

### Админ: службы доставки

- **POST /api/v1/admin/delivery-services** — создание службы. Тело: `{ "code", "name", "siteUrl?", "logoUrl?", "active?" }`. Ответ 201 + объект службы.
- **PUT /api/v1/admin/delivery-services/{id}** — обновление (все поля в теле необязательные, null = не менять). Ответ 200 + объект службы.
- **DELETE /api/v1/admin/delivery-services/{id}** — удаление. Ответ 204. Нельзя удалить службу, у которой есть связанные продукты (422).

### GET /api/v1/products

- **Параметры (все необязательные):**
  - `q` — поиск по названию (подстрока, без учёта регистра).
  - `deliveryServiceIds` — ID служб через запятую (например, `1,2`).
  - `page` — номер страницы (по умолчанию 0).
  - `size` — размер страницы 1–100 (по умолчанию 20).
  - `sort` — сортировка: `поле,направление`, поля: `name`, `price`; направление: `asc`, `desc` (например, `name,asc`).
  - Диапазоны КБЖУ: `minCalories`, `maxCalories`, `minProtein`, `maxProtein`, `minFat`, `maxFat`, `minCarbs`, `maxCarbs`.
- **Ответ:** объект с полями `items`, `page`, `size`, `totalElements`, `totalPages`. В `items` — продукты с вложенными `deliveryService` и `variants` (с КБЖУ).

При неверных параметрах возвращается **400** (VALIDATION_ERROR), при внутренних сбоях — **500** (INTERNAL_ERROR). Формат ошибок и коды описаны в `docs/DTO_SCHEMA.md`.

---

## Что сейчас готово

- Запуск API на Ktor (Netty), порт по умолчанию **8080** (или из конфига/env).
- Подключение к PostgreSQL, миграции Liquibase (таблицы `delivery_service`, `product`, `product_variant` + тестовые данные в 002_seed).
- Два бизнес-эндпоинта: список служб доставки и поиск продуктов с фильтрами и пагинацией.
- Единый формат ошибок (ErrorResponse с `requestId`, `code`, `message`, `details`, `fieldErrors`).
- RequestId в каждом запросе (заголовок `X-Request-Id` или автогенерация), логи в формате `requestId=... METHOD /path -> status`.
- OpenAPI 3.0 (YAML) и Swagger UI; в спецификации описаны 400, 422, 500 и схемы ответов.
- Docker Compose: postgres + api, healthchecks, конфигурация через `.env` (пример — `service/.env.example`).
- Индексы БД: по доставке, по поиску названия (pg_trgm/GIN), составные по КБЖУ (см. миграцию 003).

Не готово: Telegram-бот, загрузчик данных (loader), продакшен-деплой, авторизация.

---

## Индексы БД и поиск

### Индексы (миграции 001 и 003)

| Индекс | Таблица | Назначение |
|--------|---------|------------|
| **idx_product_delivery_service** | product | Фильтр по `delivery_service_id` (ручка продуктов с `deliveryServiceIds`). |
| **idx_product_name** | product | B-tree по `name` — сортировка и префиксный поиск. |
| **idx_product_name_lower_gin** | product | GIN по `lower(name)` с **pg_trgm** — поиск подстроки по названию без полного скана (`LOWER(name) LIKE '%...%'`). |
| **idx_variant_calories**, **idx_variant_protein**, **idx_variant_fat**, **idx_variant_carbs** | product_variant | Одиночные B-tree по полям КБЖУ (миграция 001). |
| **idx_variant_product_calories** | product_variant | Составной `(product_id, calories)` — выбор вариантов по продукту с фильтром по калориям. |
| **idx_variant_calories_protein** | product_variant | Составной `(calories, protein)` — частые фильтры по диапазонам калорий и белка. |
| **idx_variant_calories_fat** | product_variant | Составной `(calories, fat)` — фильтры по калориям и жирам. |

Для поиска по подстроке в названии используется расширение **pg_trgm** (триграммы). Оно создаётся в миграции 003 и позволяет эффективно выполнять `LIKE '%слово%'` и `ILIKE` по полю `lower(name)` через GIN-индекс, без полного перебора строк.

### Как выполняется поиск по БД (GET /api/v1/products)

1. **Парсинг параметров**  
   В `parseProductSearchCriteria` из query-строки берутся `q`, `deliveryServiceIds`, пагинация, сортировка и диапазоны КБЖУ.

2. **Условие WHERE (репозиторий)**  
   - **Параметр `q`:** строка разбивается по пробелам на слова. Для каждого слова продукт попадает в выборку, если слово есть **либо** в `product.name` (**LOWER(name) LIKE '%word%'`), **либо** в `product_variant.composition` (подзапрос по вариантам с `LOWER(COALESCE(composition,'')) LIKE '%word%'`). Все слова объединяются по AND.  
   - **deliveryServiceIds:** `product.delivery_service_id IN (...)` — используется индекс `idx_product_delivery_service`.  
   - **КБЖУ (min/max):** по вариантам строится условие по `calories`, `protein`, `fat`, `carbs`; продукты отбираются через подзапрос по `product_variant` — используются составные индексы по КБЖУ.

3. **Индексы при поиске по названию**  
   Условие `LOWER(product.name) LIKE '%word%'` (и подзапрос по composition) позволяет PostgreSQL использовать GIN-индекс **idx_product_name_lower_gin** для таблицы `product`, что ускоряет поиск по подстроке вместо полного скана.

4. **Ранжирование при наличии `q`**  
   Если передан параметр `q`, результаты ранжируются в памяти (до 2000 записей): сначала точная фраза в названии (100), затем все слова в названии (80), затем часть слов в названии (40), затем совпадения только в составе вариантов (20). Сортировка по убыванию ранга, при равенстве — по названию. Пагинация применяется уже к этому отсортированному списку.

5. **Без `q`**  
   Список продуктов фильтруется только по `deliveryServiceIds` и КБЖУ, сортировка — по полю из `sort` (name/price) или по названию по умолчанию. Пагинация выполняется в SQL (`LIMIT`/`OFFSET`).

Миграции: `service/src/main/resources/db/changelog/` (001 — таблицы и первые индексы, 003 — pg_trgm и составные индексы по КБЖУ). Подробнее по схеме — `docs/database-schema.md`.

---

## Как запускать (Docker Compose)

Запуск окружения — через **Docker Compose** из каталога `service/`.

### 1. Подготовка

```bash
cd service
cp .env.example .env
```

При необходимости отредактируйте `.env` (порты, пароль БД).

### 2. Запуск

```bash
docker compose --env-file .env up -d
```

Поднимаются **postgres** (порт по умолчанию **5435**) и **api** (порт **8080**). Миграции Liquibase выполняются при старте API.

### 3. Проверка

- Swagger UI: **http://localhost:8080/swagger**
- Health: **http://localhost:8080/health**

### 4. Остановка и пересборка

```bash
docker compose down
docker compose build --no-cache
docker compose --env-file .env up -d
```

### Только Postgres в Docker, API локально

```bash
docker compose up -d postgres
```

В `.env` укажите `DB_JDBC_URL=jdbc:postgresql://127.0.0.1:5435/food_helper`, затем запуск API: `./gradlew run` (порт 8080).

### Сборка и тесты

```bash
cd service
./gradlew build
./gradlew test
```

### Миграции отдельно

Миграции применяются при старте API. Для ручного запуска (CI и т.п.):

```bash
cd service
export DB_JDBC_URL=jdbc:postgresql://127.0.0.1:5435/food_helper
export DB_USER=food_user
export DB_PASSWORD=food_pass
./gradlew runMigrations
```

**Важно:** если в `.env` указан хост `postgres:5432` (для контейнера API), с хоста `./gradlew runMigrations` не подключится. Используйте переменные выше или в `.env` для локального запуска — `DB_JDBC_URL=jdbc:postgresql://127.0.0.1:5435/food_helper`.

---

## Структура репозитория (основное)

```
food-helper/
├── README.md                 # Общее описание и быстрый старт
├── README_CURRENT_STATE.md   # Этот файл — текущее состояние
├── docs/
│   ├── DTO_SCHEMA.md        # Схема DTO и формат ошибок
│   └── adr/                 # Architecture Decision Records
└── service/                 # Backend (Ktor)
    ├── docker-compose.yml   # Postgres + API
    ├── Dockerfile
    ├── .env.example
    ├── build.gradle.kts
    ├── src/main/
    │   ├── kotlin/...       # Роуты, сервисы, репозитории, конфиг
    │   └── resources/
    │       ├── application.conf
    │       ├── openapi/openapi.yaml
    │       └── db/changelog/...
    └── ...
```

Дополнительно: единая схема DTO и коды ошибок — в **docs/DTO_SCHEMA.md**; архитектурные решения — в **docs/adr/**.
