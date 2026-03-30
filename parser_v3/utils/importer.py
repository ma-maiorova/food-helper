"""
utils/importer.py — выгрузка собранных продуктов в Food Helper API батчами.

Контракт API описан в FOR_PARSER.md.
"""

import logging
import time
from dataclasses import asdict
from typing import Iterable

import requests

from models.product_item import ProductItem

logger = logging.getLogger(__name__)

# Жёсткий лимит API (менять нельзя — сервер вернёт 400, если превысить)
API_MAX_BATCH_SIZE = 500

# Рекомендованный размер батча по умолчанию
DEFAULT_BATCH_SIZE = 200


def _product_to_api_item(product: ProductItem, delivery_service_code: str) -> dict:
    """Конвертирует ProductItem в формат одного элемента запроса к /api/v1/admin/import."""
    variants = []
    for v in product.variants or []:
        variant_dict: dict = {}

        if v.manufacturer:
            variant_dict["manufacturer"] = v.manufacturer
        if v.composition:
            variant_dict["composition"] = v.composition
        if v.weight is not None:
            variant_dict["weight"] = int(v.weight)

        if v.nutrients:
            nutrients: dict = {}
            if v.nutrients.calories is not None:
                nutrients["calories"] = int(v.nutrients.calories)
            if v.nutrients.protein is not None:
                nutrients["protein"] = float(v.nutrients.protein)
            if v.nutrients.fat is not None:
                nutrients["fat"] = float(v.nutrients.fat)
            if v.nutrients.carbs is not None:
                nutrients["carbs"] = float(v.nutrients.carbs)
            if nutrients:
                variant_dict["nutrients"] = nutrients

        variants.append(variant_dict)

    # API требует хотя бы один вариант
    if not variants:
        variants = [{}]

    return {
        "name": product.name,
        "url": product.url,
        "price": max(0, int(product.price or 0)),
        "currency": "RUB",
        "variants": variants,
    }


def _send_batch(
    items: list[dict],
    delivery_service_code: str,
    api_url: str,
    api_key: str,
    retry_attempts: int = 3,
    retry_delay: float = 5.0,
) -> dict:
    """
    Отправляет один батч на /api/v1/admin/import.

    Повторяет запрос при HTTP 500 или errorCode == 'internal_error'
    (transient-ошибки по контракту).

    Возвращает dict с полями ответа или поднимает исключение после всех попыток.
    """
    payload = {
        "deliveryServiceCode": delivery_service_code,
        "items": items,
    }
    headers = {
        "Content-Type": "application/json",
        "X-Api-Key": api_key,
    }

    last_exc: Exception | None = None

    for attempt in range(1, retry_attempts + 1):
        try:
            resp = requests.post(
                api_url,
                json=payload,
                headers=headers,
                timeout=60,
            )

            # 401 — ключ неверный, retry бессмысленен
            if resp.status_code == 401:
                logger.error(
                    "API вернул 401: неверный X-Api-Key. Проверьте ADMIN_API_KEY.")
                resp.raise_for_status()

            # 400 — ошибка запроса (пустой батч, слишком много элементов и т.п.)
            if resp.status_code == 400:
                logger.error(f"API вернул 400: {resp.text}")
                resp.raise_for_status()

            # 5xx — transient, повторяем
            if resp.status_code >= 500:
                logger.warning(
                    f"Попытка {attempt}/{retry_attempts}: сервер вернул {resp.status_code}. "
                    f"Повтор через {retry_delay}с..."
                )
                time.sleep(retry_delay * attempt)
                continue

            resp.raise_for_status()
            return resp.json()

        except requests.RequestException as e:
            last_exc = e
            logger.warning(
                f"Попытка {attempt}/{retry_attempts}: сетевая ошибка: {e}")
            time.sleep(retry_delay * attempt)

    raise RuntimeError(
        f"Не удалось отправить батч после {retry_attempts} попыток. "
        f"Последняя ошибка: {last_exc}"
    )


def upload_products(
    products: Iterable[ProductItem],
    delivery_service_code: str,
    api_url: str,
    api_key: str,
    batch_size: int = DEFAULT_BATCH_SIZE,
    retry_attempts: int = 3,
    retry_delay: float = 5.0,
) -> dict:
    """
    Разбивает список продуктов на батчи и выгружает их в API.

    Args:
        products:               Итерируемый список ProductItem.
        delivery_service_code:  Код сервиса доставки (например, 'VKUSVILL').
        api_url:                Полный URL эндпоинта, например
                                'https://example.com/api/v1/admin/import'.
        api_key:                Значение заголовка X-Api-Key.
        batch_size:             Количество продуктов в одном запросе.
                                Максимум — 500 (ограничение API).
        retry_attempts:         Количество попыток при transient-ошибках.
        retry_delay:            Базовая задержка (секунды) между попытками
                                (умножается на номер попытки).

    Returns:
        Сводная статистика по всем батчам:
        {
            "batches_sent": int,
            "total_received": int,
            "duplicates_resolved": int,
            "created": int,
            "updated": int,
            "failed": int,
            "errors": list,   # все ошибки из всех батчей
        }
    """
    if batch_size > API_MAX_BATCH_SIZE:
        logger.warning(
            f"batch_size={batch_size} превышает лимит API ({API_MAX_BATCH_SIZE}). "
            f"Будет использовано {API_MAX_BATCH_SIZE}."
        )
        batch_size = API_MAX_BATCH_SIZE

    if batch_size <= 0:
        raise ValueError(f"batch_size должен быть > 0, получено: {batch_size}")

    product_list = list(products)
    logger.info(
        f"Начинаю выгрузку: {len(product_list)} продуктов, "
        f"батч={batch_size}, сервис={delivery_service_code}"
    )

    summary = {
        "batches_sent": 0,
        "total_received": 0,
        "duplicates_resolved": 0,
        "created": 0,
        "updated": 0,
        "failed": 0,
        "errors": [],
    }

    for batch_start in range(0, len(product_list), batch_size):
        batch = product_list[batch_start: batch_start + batch_size]
        batch_num = batch_start // batch_size + 1
        total_batches = (len(product_list) + batch_size - 1) // batch_size

        logger.info(
            f"Батч {batch_num}/{total_batches}: "
            f"продуктов {len(batch)} (индексы {batch_start}–{batch_start + len(batch) - 1})"
        )

        api_items = [
            _product_to_api_item(p, delivery_service_code) for p in batch
        ]

        try:
            result = _send_batch(
                items=api_items,
                delivery_service_code=delivery_service_code,
                api_url=api_url,
                api_key=api_key,
                retry_attempts=retry_attempts,
                retry_delay=retry_delay,
            )
        except Exception as e:
            logger.error(f"Батч {batch_num} провалился полностью: {e}")
            summary["failed"] += len(batch)
            continue

        summary["batches_sent"] += 1
        summary["total_received"] += result.get("totalReceived", 0)
        summary["duplicates_resolved"] += result.get("duplicatesResolved", 0)
        summary["created"] += result.get("created", 0)
        summary["updated"] += result.get("updated", 0)
        summary["failed"] += result.get("failed", 0)
        summary["errors"].extend(result.get("errors", []))

        failed_in_batch = result.get("failed", 0)
        if failed_in_batch:
            for err in result.get("errors", []):
                code = err.get("errorCode", "?")
                url = err.get("url", "?")
                msg = err.get("message", "")
                if code == "internal_error":
                    logger.warning(f"  [transient] {url}: {msg}")
                else:
                    logger.warning(f"  [data error/{code}] {url}: {msg}")

        logger.info(
            f"  → создано={result.get('created', 0)}, "
            f"обновлено={result.get('updated', 0)}, "
            f"ошибок={failed_in_batch}, "
            f"дублей сброшено={result.get('duplicatesResolved', 0)}, "
            f"за {result.get('durationMs', '?')}мс"
        )

    logger.info(
        f"Выгрузка завершена. Батчей: {summary['batches_sent']}, "
        f"создано: {summary['created']}, обновлено: {summary['updated']}, "
        f"ошибок: {summary['failed']}"
    )

    return summary
