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
    WEIGHT_CLASS = "VV23_DetailProdPageInfoDescItem"

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
                variants=self._extract_info(),
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

    def _extract_weight(self) -> int:
        try:
            element = self.driver.find_element(
                By.XPATH,
                "//div[h4[contains(text(), 'Вес')]]//div[contains(@class, 'Desc')]"
            )

            text = element.text.strip().lower()
            match = re.search(r'([\d.]+)\s*(г|кг)', text)
            weight = float(match.group(1)) if match else None
            return weight
        except NoSuchElementException:
            logger.warning("Вес не найден")
            return 0.0

    def _extract_manufacturer_composition(self) -> tuple[str, str]:
        try:
            element = self.driver.find_element(
                By.XPATH,
                "//h4[contains(., 'Состав')]/following-sibling::div"
            )

            text = element.text.strip()
            blocks = re.split(r';\s*(?=[^:]+:)', text)

            result = []

            for block in blocks:
                block = block.strip().rstrip(';')

                match = re.match(r'^([^:]+):\s*(.*)', block)

                if match:
                    result.append({
                        "manufacturer": match.group(1).strip(),
                        "description": match.group(2).strip()
                    })
        except NoSuchElementException:
            logger.warning("Состав не найден")
            return 0.0

    def _expand_composition(self):
        try:
            button = self.driver.find_element(
                By.CSS_SELECTOR,
                "button.js-vv-text-cut-showmore"
            )

            if button.is_displayed():
                self.driver.execute_script("arguments[0].click();", button)
                time.sleep(0.5)

        except NoSuchElementException:
            pass

    def _extract_compositions(self) -> dict:
        self._expand_composition()

        element = self.driver.find_element(
            By.XPATH,
            "//h4[contains(., 'Состав')]/following-sibling::div"
        )

        text = element.text.strip().replace('\xa0', ' ')

        blocks = re.split(r';\s*(?=[^:]+:)', text)

        compositions = {}

        for block in blocks:
            block = block.strip().rstrip(';')

            # 👇 ключевая проверка
            match = re.match(r'^([^:]{1,100}):\s*(.*)', block)

            if match:
                possible_manufacturer = match.group(1).strip()

                # ❗ если есть "(" — это точно НЕ производитель
                if "(" in possible_manufacturer:
                    continue

                # ❗ слишком длинное — тоже не производитель
                if len(possible_manufacturer) > 80:
                    continue

                compositions[possible_manufacturer] = match.group(2).strip()

        # 👇 fallback
        if not compositions:
            return {None: text.strip()}

        return compositions

    # def _extract_compositions(self) -> dict:
    #     self._expand_composition()  # нажимает кнопку "еще"
    #     element = self.driver.find_element(
    #         By.XPATH,
    #         "//h4[contains(., 'Состав')]/following-sibling::div"
    #     )

    #     text = element.text.strip().replace('\xa0', ' ')

    #     blocks = re.split(r';\s*(?=[^:]+:)', text)

    #     compositions = {}
    #     for block in blocks:
    #         block = block.strip().rstrip(';')

    #         match = re.match(r'^([^:]+):\s*(.*)', block)
    #         if match:
    #             manufacturer = match.group(1).strip()
    #             composition = match.group(2).strip()
    #             compositions[manufacturer] = composition

    #     if not compositions:
    #         return {None: text.strip()}

    #     return compositions

    def _extract_nutrients_one_producer(self) -> dict:
        text = self.driver.find_element(
            By.CLASS_NAME, self.NUTRIENTS_ONE_PROD_CLASS
        ).text.lower()

        def extract(pattern: str) -> float:
            match = re.search(pattern, text, re.I)
            if match:
                return float(match.group(1).replace(',', '.'))
            return 0.0

        calories = extract(r'([\d.,]+)\s*ккал')
        protein = extract(r'([\d.,]+)\s*белк')
        fat = extract(r'([\d.,]+)\s*жир')
        carbs = extract(r'([\d.,]+)\s*углевод')

        nutrients = Nutrients(
            calories=int(calories) if calories else 0,
            protein=protein,
            fat=fat,
            carbs=carbs,
        )

        comp = self._extract_compositions()
        manufacturer = list(comp)[0]  # извращение чтобы достать первый ключ
        composition = comp[manufacturer]

        weight = self._extract_weight()
        return [ProductVariant(nutrients=nutrients, weight=weight, manufacturer=manufacturer, composition=composition)]

    def _extract_nutrients_many_producers(self) -> list[ProductVariant]:
        try:
            elements = self.driver.find_elements(
                By.CLASS_NAME, self.NUTRIENTS_MANY_PROD_CLASS)

            text = ""
            for element in elements:
                if "углеводы" in element.text.lower():
                    text = element.text
                    break

            compositions = self._extract_compositions()

            pattern = re.compile(
                r'(?<!\S)(?P<manufacturer>[^:]+?)\s*:'
                r'.*?белк[и\-]*\s*(?P<protein>[\d.,]+)\s*г'
                r'.*?жир[ы\-]*\s*(?P<fat>[\d.,]+)\s*г'
                r'.*?углевод[ы\-]*\s*(?P<carbs>[\d.,]+)\s*г'
                r'.*?[\s;]\s*(?P<calories>[\d.,]+)\s*ккал',
                re.S
            )

            weight = self._extract_weight()

            products = []

            for m in pattern.finditer(text):
                manufacturer = m.group("manufacturer").strip(" .")

                nutrients = Nutrients(
                    calories=float(m.group("calories").replace(',', '.')),
                    protein=float(m.group("protein").replace(',', '.')),
                    fat=float(m.group("fat").replace(',', '.')),
                    carbs=float(m.group("carbs").replace(',', '.'))
                )

                products.append(
                    ProductVariant(
                        manufacturer=manufacturer,
                        nutrients=nutrients,
                        composition=compositions.get(
                            manufacturer),
                        weight=weight
                    )
                )

            return products

        except Exception as e:
            print(e)
            return []

    def _extract_info(self) -> list[ProductVariant]:
        try:
            return self._extract_nutrients_one_producer()
        except NoSuchElementException:
            return self._extract_nutrients_many_producers()
        except NoSuchElementException:
            logger.warning("Нет данных КБЖУ")
            return {}
