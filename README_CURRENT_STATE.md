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

### GET /api/v1/delivery-services

- **Параметры:**  
  - `active` (query, необязательный) — фильтр по активности: `true`/`false` или `1`/`0`.
- **Ответ:** массив объектов служб доставки (id, code, name, siteUrl, logoUrl, active).

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
