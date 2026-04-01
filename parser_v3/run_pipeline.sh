#!/bin/bash
# =============================================================
# run_pipeline.sh — полный пайплайн парсинга для одного запуска
#
# Переменные окружения:
#   PARSER_SITES          — сайты через пробел (по умолч.: "vkusvill lavka")
#   PARSER_PRODUCT_COUNT  — кол-во продуктов на сайт (по умолч.: 1000)
#   PARSER_LINK_PAGES     — кол-во страниц для сбора ссылок (по умолч.: 55)
#   PARSER_BATCH_SIZE     — размер батча при выгрузке (по умолч.: 200)
#   FOOD_HELPER_API_URL   — URL эндпоинта /api/v1/admin/import (обязательно)
#   FOOD_HELPER_API_KEY   — значение заголовка X-Api-Key (обязательно)
# =============================================================

set -e

SITES="${PARSER_SITES:-vkusvill lavka}"
COUNT="${PARSER_PRODUCT_COUNT:-1000}"
PAGES="${PARSER_LINK_PAGES:-55}"
BATCH_SIZE="${PARSER_BATCH_SIZE:-200}"
API_URL="${FOOD_HELPER_API_URL:-}"
API_KEY="${FOOD_HELPER_API_KEY:-}"

echo "[PIPELINE] $(date '+%Y-%m-%d %H:%M:%S') Старт пайплайна"
echo "[PIPELINE] Сайты: $SITES | Продуктов: $COUNT | Страниц: $PAGES | Батч: $BATCH_SIZE"

if [ -z "$API_URL" ] || [ -z "$API_KEY" ]; then
    echo "[PIPELINE] ОШИБКА: FOOD_HELPER_API_URL и FOOD_HELPER_API_KEY должны быть заданы!" >&2
    exit 1
fi

# Запускаем виртуальный дисплей (нужен для браузера)
if ! pgrep -x Xvfb > /dev/null 2>&1; then
    Xvfb :99 -screen 0 1920x1080x24 -ac &
    sleep 2
fi
export DISPLAY=:99

ERRORS=0

for SITE in $SITES; do
    echo ""
    echo "[PIPELINE] ===== Сайт: $SITE ====="

    LINKS_FILE="/app/data/${SITE}_links.csv"
    PRODUCTS_FILE="/app/data/${SITE}_products.json"

    # --- Шаг 1: Сбор ссылок ---
    echo "[PIPELINE] Шаг 1: сбор ссылок ($SITE, страниц: $PAGES)"
    if python main.py links --site "$SITE" --output "$LINKS_FILE" --pages "$PAGES"; then
        echo "[PIPELINE] Ссылки собраны: $LINKS_FILE"
    else
        echo "[PIPELINE] ОШИБКА при сборе ссылок для $SITE, пропускаю" >&2
        ERRORS=$((ERRORS + 1))
        continue
    fi

    # --- Шаг 2: Парсинг карточек ---
    echo "[PIPELINE] Шаг 2: парсинг карточек ($SITE, продуктов: $COUNT)"
    if python main.py parse \
        --site "$SITE" \
        --input "$LINKS_FILE" \
        --output "$PRODUCTS_FILE" \
        --count "$COUNT" \
        --fmt json; then
        echo "[PIPELINE] Карточки спарсены: $PRODUCTS_FILE"
    else
        echo "[PIPELINE] ОШИБКА при парсинге карточек для $SITE, пропускаю" >&2
        ERRORS=$((ERRORS + 1))
        continue
    fi

    # --- Шаг 3: Выгрузка в API ---
    echo "[PIPELINE] Шаг 3: выгрузка в API ($SITE, батч: $BATCH_SIZE)"
    if python main.py upload \
        --site "$SITE" \
        --input "$PRODUCTS_FILE" \
        --batch-size "$BATCH_SIZE" \
        --api-url "$API_URL" \
        --api-key "$API_KEY"; then
        echo "[PIPELINE] Выгрузка завершена: $SITE"
    else
        echo "[PIPELINE] ОШИБКА при выгрузке для $SITE" >&2
        ERRORS=$((ERRORS + 1))
    fi
done

echo ""
echo "[PIPELINE] $(date '+%Y-%m-%d %H:%M:%S') Пайплайн завершён. Ошибок сайтов: $ERRORS"
exit $ERRORS
