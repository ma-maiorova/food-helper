#!/bin/bash
set -e

# Запускаем виртуальный дисплей
Xvfb :99 -screen 0 1920x1080x24 -ac &
export DISPLAY=:99

# Ждём пока Xvfb поднимется
sleep 1

exec python main.py "$@"