import time
import re
import logging
import utils.config as config
from selenium.common.exceptions import TimeoutException, NoSuchElementException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from sites.base_parser import BaseParser
from models.product_item import Nutrients, ProductVariant, ProductItem


logger = logging.getLogger(__name__)


class VkusvillParser(BaseParser):
    """
    Парсер сайта https://vkusvill.ru.

    Собирает ссылки на товары и извлекает данные карточек.
    """

    PRODUCT_CARD_CLASS = "ProductCard__imageInner"
    PRODUCT_TITLE_CLASS = "Product__title"
    PRODUCT_PRICE_CLASS = "Product__price"
    NUTRIENTS_ONE_PROD_CLASS = "VV23_DetailProdPageAccordion__EnergyWrap"
    NUTRIENTS_MANY_PROD_CLASS = "VV23_DetailProdPageInfoDescItem__Desc"

    def __init__(self, driver, base_url="https://vkusvill.ru/"):
        super().__init__(base_url)
        self.driver = driver

    def get_product_links(self, max_pages=55) -> set:
        all_links = set()

        for category_url in config.get_section_links(config.data):
            logger.info(f"Обработка раздела: {category_url}")
            links = self._parse_paginated_links(category_url, max_pages)
            logger.info(f"Ссылок найдено: {len(links)}")
            all_links.update(links)

        logger.info(f"Итого собрано ссылок: {len(all_links)}")
        return all_links

    def _parse_paginated_links(self, base_url, max_pages):
        links = set()

        for page in range(1, max_pages + 1):
            url = base_url if page == 1 else f"{base_url}?PAGEN_1={page}"

            try:
                self.driver.get(url)
                WebDriverWait(self.driver, 10).until(
                    EC.presence_of_element_located(
                        (By.CLASS_NAME, self.PRODUCT_CARD_CLASS))
                )
            except TimeoutException:
                logger.warning(
                    f"Страница {page} не загрузилась и была пропущена")
                continue
            except Exception as e:
                logger.error(f"Ошибка загрузки страницы {page}: {e}")
                continue

            cards = self.driver.find_elements(
                By.CLASS_NAME, self.PRODUCT_CARD_CLASS)

            for card in cards:
                try:
                    href = card.find_element(
                        By.TAG_NAME, "a").get_attribute("href")
                    if href:
                        links.add(href)
                except NoSuchElementException:
                    logger.debug("Карточка пропущена из-за отсутствия ссылки")
                except Exception as e:
                    logger.error(f"Ошибка при извлечении ссылки: {e}")

            logger.info(
                f"Страница {page}: карточек={len(cards)}, всего ссылок собрано={len(links)}")
            time.sleep(0.4)

        return links

    def parse_product(self, url: str) -> dict | None:
        try:
            self.driver.get(url)
            logger.info(f"Обрабатываю: {url}")
            card = ProductItem(
                name=self._get_name(),
                url=url,
                price=self._get_price(),
                variants=self._extract_nutrients(),
                shop="vkusvill"
            )
            logger.info(f"Обработан: {card.name}")
            return card

        except TimeoutException:
            logger.warning(f"Время на загрузку товара истекло: {url}")
            return None
        except Exception as e:
            logger.error(f"Ошибка обработки {url}: {e}")
            return None

    def _get_name(self) -> str:
        el = WebDriverWait(self.driver, 10).until(
            EC.presence_of_element_located(
                (By.CLASS_NAME, self.PRODUCT_TITLE_CLASS))
        )
        return el.text.strip()

    def _get_price(self) -> float:
        try:
            text = self.driver.find_element(
                By.CLASS_NAME, self.PRODUCT_PRICE_CLASS).text
            match = re.search(r'^\d+', text)
            price = int(match.group(0))
            return price
        except NoSuchElementException:
            logger.warning("Цена не найдена")
            return 0.0

    def _extract_nutrients_one_producer(self) -> dict:
        text = self.driver.find_element(
            By.CLASS_NAME, self.NUTRIENTS_ONE_PROD_CLASS).text.lower()
        pattern = re.compile(
            r'(?P<calories>[\d.]+)\s*ккал.*?'
            r'(?P<protein>[\d.]+)\s*белк.*?'
            r'(?P<fat>[\d.]+)\s*жир.*?'
            r'(?P<carbs>[\d.]+)\s*углевод',
            re.S | re.I
        )

        m = pattern.search(text)
        nutrients = Nutrients(
            calories=int(float(m.group("calories"))) if m else None,
            protein=float(m.group("protein")) if m else None,
            fat=float(m.group("fat")) if m else None,
            carbs=float(m.group("carbs")) if m else None,
        ) if m else Nutrients()

        return [ProductVariant(nutrients=nutrients)]

    def _extract_nutrients_many_producers(self) -> dict:
        try:
            elements = self.driver.find_elements(
                By.CLASS_NAME, self.NUTRIENTS_MANY_PROD_CLASS)
            for element in elements:
                if "углеводы" in element.text.lower():
                    text = element.text
                    break

            pattern = re.compile(
                r'(?<!\S)(?P<manufacturer>[^:]+?)\s*:'
                r'.*?белк[и\-]*\s*(?P<protein>[\d.,]+)\s*г'
                r'.*?жир[ы\-]*\s*(?P<fat>[\d.,]+)\s*г'
                r'.*?углевод[ы\-]*\s*(?P<carbs>[\d.,]+)\s*г'
                r'.*?[\s;]\s*(?P<calories>[\d.,]+)\s*ккал',
                re.S
            )

            products = []
            for m in pattern.finditer(text):

                nutrients = Nutrients(
                    calories=float(m.group("calories").replace(',', '.')),
                    protein=float(m.group("protein").replace(',', '.')),
                    fat=float(m.group("fat").replace(',', '.')),
                    carbs=float(m.group("carbs").replace(',', '.')))

                products.append(
                    ProductVariant(
                        manufacturer=m.group("manufacturer").strip(" ."),
                        nutrients=nutrients
                    )
                )
            return products

        except (NoSuchElementException, UnboundLocalError):
            logger.warning("Нет данных КБЖУ")
            return []

    def _extract_nutrients(self) -> list[ProductVariant]:
        try:
            return self._extract_nutrients_one_producer()
        except NoSuchElementException:
            return self._extract_nutrients_many_producers()
        except NoSuchElementException:
            logger.warning("Нет данных КБЖУ")
            return {}
