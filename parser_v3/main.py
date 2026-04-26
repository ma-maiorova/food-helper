import argparse
import os
import time
from utils.browser import get_driver
from sites import SITES
from utils.file_handler import save_data, load_data, save_products, load_products
from utils.importer import upload_products, DEFAULT_BATCH_SIZE, API_MAX_BATCH_SIZE

import logging
logging.basicConfig()
logging.getLogger().setLevel(logging.INFO)
logger = logging.getLogger(__name__)


DELIVERY_SERVICE_CODES = {
    "vkusvill": "VKUSVILL",
    "lavka": "YANDEX_LAVKA",
}


if __name__ == "__main__":
    arg = argparse.ArgumentParser(
        description="Food Helper parser — сбор и выгрузка продуктов."
    )
    arg.add_argument(
        "task",
        choices=["links", "parse", "upload"],
        help=(
            "links  — собрать ссылки на товары;\n"
            "parse  — спарсить карточки по ссылкам;\n"
            "upload — выгрузить сохранённые продукты в API."
        ),
    )
    arg.add_argument("--site", default="vkusvill", choices=SITES)
    arg.add_argument("--output", default="output.csv")
    arg.add_argument("--pages", type=int, default=55)
    arg.add_argument("--input", default="vkusvill_ready_foods.csv")
    arg.add_argument("--fmt", default="csv")

    arg.add_argument("--count", default=5)

    # --- параметры выгрузки ---
    arg.add_argument(
        "--batch-size",
        type=int,
        default=DEFAULT_BATCH_SIZE,
        metavar="N",
        help=(
            f"Размер одного батча при выгрузке в API "
            f"(по умолчанию: {DEFAULT_BATCH_SIZE}, максимум: {API_MAX_BATCH_SIZE})."
        ),
    )
    arg.add_argument(
        "--api-url",
        default=os.environ.get("FOOD_HELPER_API_URL", ""),
        help="URL эндпоинта импорта, напр. https://example.com/api/v1/admin/import. "
             "Можно задать через переменную окружения FOOD_HELPER_API_URL.",
    )
    arg.add_argument(
        "--api-key",
        default=os.environ.get("FOOD_HELPER_API_KEY", ""),
        help="Значение заголовка X-Api-Key. "
             "Можно задать через переменную окружения FOOD_HELPER_API_KEY.",
    )
    arg.add_argument(
        "--delivery-service",
        default=None,
        help=(
            "Код сервиса доставки для API (напр. VKUSVILL, YANDEX_LAVKA). "
            "По умолчанию определяется автоматически по --site."
        ),
    )

    args = arg.parse_args()

    # ------------------------------------------------------------------ upload
    if args.task == "upload":
        api_url = args.api_url
        api_key = args.api_key

        if not api_url:
            arg.error(
                "Для задачи upload нужен --api-url "
                "или переменная окружения FOOD_HELPER_API_URL."
            )
        if not api_key:
            arg.error(
                "Для задачи upload нужен --api-key "
                "или переменная окружения FOOD_HELPER_API_KEY."
            )

        delivery_service = (
            args.delivery_service or DELIVERY_SERVICE_CODES.get(args.site)
        )
        if not delivery_service:
            arg.error(
                f"Не удалось определить delivery_service для сайта '{args.site}'. "
                "Укажите --delivery-service явно."
            )

        products = load_products(args.input)
        logger.info(f"Загружено {len(products)} продуктов из {args.input}")

        summary = upload_products(
            products=products,
            delivery_service_code=delivery_service,
            api_url=api_url,
            api_key=api_key,
            batch_size=args.batch_size,
        )

        logger.info(
            f"Итог: батчей={summary['batches_sent']}, "
            f"создано={summary['created']}, "
            f"обновлено={summary['updated']}, "
            f"ошибок={summary['failed']}"
        )
        raise SystemExit(0)

    # --------------------------------------------------------- links / parse
    driver = get_driver(logger)
    parser = SITES[args.site](driver)
    try:
        if args.task == "links":
            links = parser.get_product_links(max_pages=args.pages)
            save_data(list(links), args.output)

        elif args.task == "parse":
            links = load_data(args.input, format="csv")[:int(args.count)]
            total = len(links)
            logger.info(f"Начинаю парсинг {total} карточек")
            parse_start = time.monotonic()
            data = []
            for i, url in enumerate(links, 1):
                card_start = time.monotonic()
                result = parser.parse_product(url)
                card_elapsed = time.monotonic() - card_start
                if result:
                    data.append(result)
                elapsed = time.monotonic() - parse_start
                avg = elapsed / i
                eta = avg * (total - i)
                logger.info(
                    f"[{i}/{total}] {card_elapsed:.1f}с | прошло {elapsed:.0f}с | "
                    f"осталось ~{eta:.0f}с | успешно: {len(data)}"
                )
            logger.info(
                f"Парсинг завершён за {time.monotonic() - parse_start:.0f}с — "
                f"товаров: {len(data)}/{total}"
            )
            save_products([i for i in data if i],
                          filepath=args.output, file_format=args.fmt)
    finally:
        driver.quit()
