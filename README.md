# Food Helper — поиск готовой еды по КБЖУ

Telegram-бот для поиска готовой еды из сервисов доставки с фильтрацией по КБЖУ (калории, белки, жиры, углеводы).

---

## Что делает проект

Пользователь открывает бота в Telegram, выбирает сервис доставки и задаёт диапазоны КБЖУ. Бот возвращает список подходящих товаров с ценой и прямой ссылкой.

Данные о товарах собираются автоматически парсером каждую ночь и хранятся в PostgreSQL. Все запросы бота идут через REST API.

---

## Поддерживаемые сервисы доставки

- ВкусВилл
- Яндекс Лавка

---

## Технологии

| Компонент | Стек |
|---|---|
| Telegram-бот | Python 3.12, aiogram 3, aiohttp |
| REST API | Kotlin 2, Ktor 2, Netty, Exposed ORM |
| База данных | PostgreSQL 16, Liquibase (миграции) |
| Парсер | Python 3.12, Selenium, undetected-chromedriver, Chrome 145 |
| Инфраструктура | Docker Compose, nginx, Prometheus, Grafana |

---

## Архитектура

```
Telegram ←──polling──► bot (aiogram)
                            │
                            ▼ HTTP
                        nginx :80
                            │
                            ▼ proxy_pass
                        api (Ktor) :8080
                            │
                            ▼ HikariCP
                        PostgreSQL :5432

parser-scheduler (cron 00:00 UTC)
    │  Selenium + Chrome
    │  ВкусВилл, Яндекс Лавка
    └──────────────────────► api POST /api/v1/admin/import
```

**Сервисы:**

- **bot** — Telegram-бот на polling. Читает данные через REST API, не обращается к БД напрямую.
- **api** — Ktor REST API. Хранит продукты, обслуживает поиск с фильтрацией и ранжированием. Ждёт healthcheck postgres перед стартом, прогоняет Liquibase-миграции при запуске.
- **nginx** — единственная публичная точка входа. Проксирует `/api/*` на api, раздаёт SPA-фронт, закрывает `/metrics` от публичного доступа.
- **postgres** — PostgreSQL 16. Три таблицы: `delivery_service`, `product`, `product_variant`.
- **parser-scheduler** — Python-контейнер с Chrome и cron. Каждую ночь в 00:00 UTC собирает товары с сайтов и загружает их в API батчами.

---

## Поиск и ранжирование

Параметр `q` разбивается на токены (AND-семантика). Каждый токен проверяется против `product.name` и `product_variant.composition`.

| Ранг | Условие |
|---|---|
| 100 | Точная фраза в названии |
| 80 | Все токены в названии (любой порядок) |
| 50 | Часть токенов в названии, остальные в составе |
| 30 | Все токены только в составе |

Пагинация стабильна: `rank DESC, name ASC, id ASC`.

---

## API

Swagger UI доступен по `/swagger-ui/` после запуска.

| Метод | Путь | Описание |
|---|---|---|
| GET | `/api/v1/products` | Поиск с фильтрами КБЖУ и пагинацией |
| GET | `/api/v1/delivery-services` | Список сервисов доставки |
| POST | `/api/v1/admin/import` | Импорт товаров (требует `X-Api-Key`) |
| GET | `/health` | Liveness probe |
| GET | `/ready` | Readiness probe (проверяет БД) |
| GET | `/metrics` | Prometheus метрики |

---

## Быстрый старт (локально)

```bash
git clone https://github.com/ma-maiorova/food-helper-filter.git
cd food-helper-filter/service

# Заполни BOT_TOKEN в .env.local
nano .env.local

docker compose --env-file .env.local up -d --build
```

После запуска:
- Сайт + API: `http://localhost:8090`
- Swagger UI: `http://localhost:8090/swagger-ui/`
- Postgres: `localhost:5433` (для DBeaver / TablePlus)

Подробная инструкция по VPS и архитектура балансировки — в **[DEPLOY.md](DEPLOY.md)**.

---

## Структура репозитория

```
food-helper-filter/
├── bot/                    # Telegram-бот (Python, aiogram)
│   ├── main.py             # точка входа, polling
│   ├── config.py           # pydantic-settings: BOT_TOKEN, API_BASE_URL
│   ├── handlers/           # обработчики команд и колбэков
│   ├── services/           # HTTP-клиент к API
│   ├── keyboards/          # inline и reply клавиатуры
│   ├── states/             # FSM-состояния (aiogram)
│   ├── Dockerfile
│   └── requirements.txt
│
├── parser_v3/              # Парсер (Python, Selenium, Chrome)
│   ├── main.py             # CLI: links / parse / upload
│   ├── sites/              # парсеры: VkusvillParser, LavkaParser
│   ├── models/             # ProductItem, ProductVariant, Nutrients
│   ├── utils/              # browser, file_handler, importer, config
│   ├── run_pipeline.sh     # полный пайплайн (links → parse → upload)
│   ├── scheduler_entrypoint.sh  # cron-демон
│   ├── entrypoint.sh       # запуск с Xvfb
│   └── Dockerfile
│
├── service/                # REST API (Kotlin, Ktor)
│   ├── src/main/kotlin/
│   │   ├── Application.kt  # точка входа, конфигурация Ktor
│   │   ├── api/            # routes, DTOs, auth, validation, errors
│   │   ├── service/        # бизнес-логика, SearchRanking
│   │   ├── repository/     # Exposed ORM, Tables.kt
│   │   ├── domain/         # Product, DeliveryService, Nutrients, ProductVariant
│   │   ├── config/         # DB, metrics, request-id
│   │   └── db/             # Liquibase runner
│   ├── src/main/resources/
│   │   ├── application.conf
│   │   └── db/changelog/   # Liquibase миграции
│   ├── docker-compose.yml  # основной стек
│   ├── monitoring/         # Prometheus + Grafana
│   ├── docs/nginx.docker.conf
│   ├── .env.example        # шаблон переменных
│   ├── .env.local          # локальные значения (не в git)
│   ├── .env.prod           # prod-значения (не в git)
│   └── Dockerfile
│
└── DEPLOY.md               # архитектура и инструкция по VPS
```

---

## Мониторинг

Prometheus собирает метрики с `/metrics`. Grafana визуализирует.

Метрики API: `product_search_requests_total`, `product_import_requests_total`, `product_import_items_total`, `product_import_duration` и HTTP-метрики Ktor.

Запуск мониторинга:
```bash
cd service
docker compose -f monitoring/docker-compose.yml --env-file .env.prod up -d
```

Доступ через SSH-туннель:
```bash
ssh -L 3000:127.0.0.1:3000 user@VPS   # Grafana → localhost:3000
ssh -L 9090:127.0.0.1:9090 user@VPS   # Prometheus → localhost:9090
```

---

## Переменные окружения

Все переменные описаны с комментариями в `service/.env.example`.

Обязательные для запуска:

| Переменная | Для кого | Описание |
|---|---|---|
| `POSTGRES_PASSWORD` | postgres, api | Пароль БД |
| `ADMIN_API_KEY` | api | Ключ для admin-эндпоинтов |
| `FOOD_HELPER_API_KEY` | parser | Должен совпадать с `ADMIN_API_KEY` |
| `BOT_TOKEN` | bot | Токен из @BotFather |
