#!/bin/bash
# =============================================================
# scheduler_entrypoint.sh — точка входа для cron-контейнера
#
# Сохраняет переменные окружения, настраивает crontab на 00:00,
# затем запускает cron в foreground (контейнер не выходит).
# =============================================================

set -e

echo "[SCHEDULER] $(date '+%Y-%m-%d %H:%M:%S') Инициализация планировщика..."
echo "[SCHEDULER] Сайты  : ${PARSER_SITES:-vkusvill lavka}"
echo "[SCHEDULER] Продуктов/сайт: ${PARSER_PRODUCT_COUNT:-1000}"
echo "[SCHEDULER] Страниц ссылок : ${PARSER_LINK_PAGES:-55}"
echo "[SCHEDULER] Батч-размер    : ${PARSER_BATCH_SIZE:-200}"
echo "[SCHEDULER] API URL        : ${FOOD_HELPER_API_URL:-(не задан!)}"

# -----------------------------------------------------------------------
# Сохраняем переменные окружения — cron запускается без них по умолчанию
# -----------------------------------------------------------------------
printenv | while IFS= read -r line; do
    # пропускаем служебные переменные bash
    case "$line" in
        BASHOPTS=*|BASH_VERSINFO=*|EUID=*|PPID=*|SHELLOPTS=*|UID=*|SHLVL=*) continue ;;
    esac
    echo "export $(echo "$line" | sed 's/=/="/' | sed 's/$/"/')"
done > /app/.cron_env

# -----------------------------------------------------------------------
# Устанавливаем crontab: 00:00 каждый день
# -----------------------------------------------------------------------
CRON_JOB="0 0 * * * . /app/.cron_env && /app/run_pipeline.sh >> /proc/1/fd/1 2>&1"
echo "$CRON_JOB" | crontab -
echo "[SCHEDULER] Crontab установлен: '$CRON_JOB'"

echo "[SCHEDULER] Следующий запуск — сегодня/завтра в 00:00 UTC"
echo "[SCHEDULER] Запуск вручную: docker compose exec parser-scheduler /app/run_pipeline.sh"
echo "[SCHEDULER] Cron запущен, жду..."

# Запускаем cron в foreground чтобы контейнер не завершался
exec cron -f
