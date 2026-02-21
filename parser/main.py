import argparse
from utils.browser import get_driver
from sites.vkusvill import VkusvillParser
from sites.lavka import LavkaParser
from utils.file_handler import save_data, load_data, save_products

import logging
logging.basicConfig()
logging.getLogger().setLevel(logging.INFO)
logger = logging.getLogger(__name__)


if __name__ == "__main__":
    arg = argparse.ArgumentParser()
    arg.add_argument("task", choices=["links", "parse"])
    arg.add_argument("--output", default="output.csv")
    arg.add_argument("--pages", type=int, default=55)
    arg.add_argument("--input", default="vkusvill_ready_foods.csv")
    arg.add_argument("--count", default=5)
    args = arg.parse_args()

    driver = get_driver(logger)
    parser = LavkaParser(driver)

    if args.task == "links":
        links = parser.get_product_links(max_pages=args.pages)
        save_data(list(links), args.output)

    elif args.task == "parse":
        pass
        links = load_data(args.input, format="csv")[:int(args.count)]
        data = [parser.parse_product(url) for url in links]
        save_products([i for i in data if i],
                      filepath=args.output, file_format="csv")

    driver.quit()
