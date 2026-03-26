# Food Helper Parser — Docker

## Структура проекта

```
.
├── Dockerfile
├── docker-compose.yml
├── .env.example          # шаблон переменных окружения
├── .dockerignore
├── requirements.txt
├── main.py
├── data/                 # создаётся автоматически, сюда пишутся CSV/JSON
├── models/
│   └── product_item.py
├── sites/
│   ├── __init__.py
│   ├── base_parser.py
│   ├── vkusvill.py
│   └── lavka.py
└── utils/
    ├── browser.py
    ├── config.py
    ├── file_handler.py
    └── importer.py
```

## Быстрый старт

### 1. Подготовка

```bash
cp .env.example .env
# заполни FOOD_HELPER_API_URL и FOOD_HELPER_API_KEY в .env
mkdir -p data
```

### 2. Сборка образа

```bash
docker build -t food-helper-parser .
```

Или через compose:

```bash
docker compose build
```

---

## Запуск задач

Все три задачи запускаются одной командой — меняется только аргумент.
Выходные файлы появляются в папке `./data` на хосте.

### Шаг 1 — Сбор ссылок

```bash
docker run --rm \
  -v "$(pwd)/data:/app/data" \
  food-helper-parser \
  links --site vkusvill --output data/vkusvill_links.csv --pages 55
```

```bash
docker run --rm \
  -v "$(pwd)/data:/app/data" \
  food-helper-parser \
  links --site lavka --output data/lavka_links.csv
```

### Шаг 2 — Парсинг карточек

Важно сохранить данные в `json` формате.

```bash
docker run --rm \
  -v "$(pwd)/data:/app/data" \
  food-helper-parser \
  parse --site vkusvill \
        --input data/vkusvill_links.csv \
        --output data/vkusvill_products.json \
        --count 1000 \
        --fmt json
```

### Шаг 3 — Выгрузка в API

Выгружаем данные из `json`, из `csv` плохо конвертируется для выгрузки на сервер

```bash
docker run --rm \
  -v "$(pwd)/data:/app/data" \
  --env-file .env \
  food-helper-parser \
  upload --site vkusvill \
         --input data/vkusvill_products.json \
         --batch-size 200
```

---

## Использование в чужом docker-compose

Если соратница подключает парсер как сервис в свой `docker-compose.yml`:

```yaml
services:
  parser:
    image: food-helper-parser:latest   # собери образ заранее: docker build -t food-helper-parser .
    volumes:
      - parser_data:/app/data
    environment:
      FOOD_HELPER_API_URL: ${FOOD_HELPER_API_URL}
      FOOD_HELPER_API_KEY: ${FOOD_HELPER_API_KEY}
    # команду передавать при запуске:
    # docker compose run --rm parser links --site vkusvill --output data/links.csv

volumes:
  parser_data:
```

Либо образ можно опубликовать в registry (Docker Hub, Yandex Container Registry и т.д.)
и передать соратнице тег вместо локальной сборки.

---

## Переменные окружения

| Переменная            | Описание                              | Обязательна для |
|-----------------------|---------------------------------------|-----------------|
| `FOOD_HELPER_API_URL` | URL эндпоинта импорта                 | `upload`        |
| `FOOD_HELPER_API_KEY` | Значение заголовка `X-Api-Key`        | `upload`        |

Можно передавать через `--env-file .env`, флаги `--api-url` / `--api-key`,
или прописать в `docker-compose.yml`.
