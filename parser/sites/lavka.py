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


class LavkaParser(BaseParser):
    """
    Парсер сайта https://lavka.yandex.ru

    Собирает ссылки на товары и извлекает данные карточек.
    """

    PRODUCT_CARD_CLASS = "plate__FHOgz"
    PRODUCT_TITLE_CLASS = "product-title"
    PRODUCT_PRICE_CLASS = "Product__price"
    NUTRIENTS_ONE_PROD_CLASS = "VV23_DetailProdPageAccordion__EnergyWrap"
    NUTRIENTS_MANY_PROD_CLASS = "VV23_DetailProdPageInfoDescItem__Desc"

    PRODUCT_TITLE_SELECTOR = "h1[data-testid='product-title']"

    def __init__(self, driver, base_url="https://lavka.yandex.ru"):
        super().__init__(base_url)
        self.driver = driver

    def get_product_links(self, max_pages=55) -> set:
        all_links = set()

        for category_url in config.get_section_links(config.data_lavka):
            logger.info(f"Обработка раздела: {category_url}")
            links = self._parse_links(category_url)
            logger.info(f"Ссылок найдено: {len(links)}")
            all_links.update(links)

        logger.info(f"Итого собрано ссылок: {len(all_links)}")
        return all_links

    def _parse_links(self, base_url):
        links = set()

        url = base_url

        try:
            self.driver.get(url)
            time.sleep(10)
            WebDriverWait(self.driver, 10).until(
                EC.presence_of_element_located(
                    (By.CLASS_NAME, self.PRODUCT_CARD_CLASS))
            )
            logger.info("Элемент успешно найден")
        except TimeoutException:
            logger.warning(
                "Страница не загрузилась и была пропущена")
        except Exception as e:
            logger.error(f"Ошибка загрузки страницы: {e}")

        cards = self.driver.find_elements(
            By.CLASS_NAME, self.PRODUCT_CARD_CLASS)

        for card in cards:
            try:
                href = card.get_attribute("href")
                if href:
                    links.add(href)
            except NoSuchElementException:
                logger.debug("Карточка пропущена из-за отсутствия ссылки")
            except Exception as e:
                logger.error(f"Ошибка при извлечении ссылки: {e}")
        print(links)
        return links

    def parse_product(self, url: str) -> dict | None:
        try:
            self.driver.get(url)
            logger.info(f"Обрабатываю: {url}")
            card = ProductItem(
                name=self._get_name(),
                url=url,
                price=self._get_price(),
                variants=self._extract_nutrients()
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
                (By.CSS_SELECTOR, self.PRODUCT_TITLE_SELECTOR))
        )
        return el.text.strip()

    def _get_price(self) -> float:
        try:
            # text = self.driver.find_element(
            #     By.CLASS_NAME, self.PRODUCT_PRICE_CLASS).text
            # match = re.search(r'^\d+', text)
            # price = int(match.group(0))
            el = WebDriverWait(self.driver, 10).until(
                EC.presence_of_element_located(
                    (By.CSS_SELECTOR, "[data-testid='price-old-text']"))
            )
            text = el.text.strip()
            price = float(''.join(filter(str.isdigit, text)))
            print(price)
            return price
        except NoSuchElementException:
            logger.warning("Цена не найдена")
            return 0.0

    def _extract_nutrients(self) -> dict:
        try:
            nutrients = {}

            # Маппинг названий и XPath запросов
            mappings = {
                'calories': ('ккал', 'калорий'),
                'protein': ('белки', 'белк'),
                'fat': ('жиры', 'жир'),
                'carbs': ('углеводы', 'углевод')
            }

            for key, search_terms in mappings.items():
                for term in search_terms:
                    try:
                        # Ищем dt с нужным текстом и берем следующее dd
                        value_element = self.driver.find_element(
                            By.XPATH,
                            f"//dt[contains(text(), '{term}')]/following-sibling::dd[1]"
                        )

                        value_str = value_element.text.strip().replace(',', '.')

                        if key == 'calories':
                            nutrients[key] = int(float(value_str))
                        else:
                            nutrients[key] = float(value_str)
                        break
                    except:  # noqa: E722
                        continue

            nutrients_obj = Nutrients(
                calories=nutrients.get('calories'),
                protein=nutrients.get('protein'),
                fat=nutrients.get('fat'),
                carbs=nutrients.get('carbs')
            )

            return [ProductVariant(nutrients=nutrients_obj)]

        except Exception as e:
            logger.error(f"Ошибка при парсинге КБЖУ: {e}")
            return [ProductVariant(nutrients=Nutrients())]
    # def _extract_nutrients_one_producer(self) -> dict:
    #     text = self.driver.find_element(
    #         By.CLASS_NAME, self.NUTRIENTS_ONE_PROD_CLASS).text.lower()
    #     pattern = re.compile(
    #         r'(?P<calories>[\d.]+)\s*ккал.*?'
    #         r'(?P<protein>[\d.]+)\s*белк.*?'
    #         r'(?P<fat>[\d.]+)\s*жир.*?'
    #         r'(?P<carbs>[\d.]+)\s*углевод',
    #         re.S | re.I
    #     )

    #     m = pattern.search(text)
    #     nutrients = Nutrients(
    #         calories=int(float(m.group("calories"))) if m else None,
    #         protein=float(m.group("protein")) if m else None,
    #         fat=float(m.group("fat")) if m else None,
    #         carbs=float(m.group("carbs")) if m else None,
    #     ) if m else Nutrients()

    #     return [ProductVariant(nutrients=nutrients)]

    # def _extract_nutrients_many_producers(self) -> dict:
    #     try:
    #         elements = self.driver.find_elements(
    #             By.CLASS_NAME, self.NUTRIENTS_MANY_PROD_CLASS)
    #         for element in elements:
    #             if "углеводы" in element.text.lower():
    #                 text = element.text
    #                 break

    #         pattern = re.compile(
    #             r'(?<!\S)(?P<manufacturer>[^:]+?)\s*:'
    #             r'.*?белк[и\-]*\s*(?P<protein>[\d.,]+)\s*г'
    #             r'.*?жир[ы\-]*\s*(?P<fat>[\d.,]+)\s*г'
    #             r'.*?углевод[ы\-]*\s*(?P<carbs>[\d.,]+)\s*г'
    #             r'.*?[\s;]\s*(?P<calories>[\d.,]+)\s*ккал',
    #             re.S
    #         )

    #         products = []
    #         for m in pattern.finditer(text):

    #             nutrients = Nutrients(
    #                 calories=float(m.group("calories").replace(',', '.')),
    #                 protein=float(m.group("protein").replace(',', '.')),
    #                 fat=float(m.group("fat").replace(',', '.')),
    #                 carbs=float(m.group("carbs").replace(',', '.')))

    #             products.append(
    #                 ProductVariant(
    #                     manufacturer=m.group("manufacturer").strip(" ."),
    #                     nutrients=nutrients
    #                 )
    #             )
    #         return products

    #     except (NoSuchElementException, UnboundLocalError):
    #         logger.warning("Нет данных КБЖУ")
    #         return []

    # def _extract_nutrients(self) -> list[ProductVariant]:
    #     try:
    #         return self._extract_nutrients_one_producer()
    #     except NoSuchElementException:
    #         return self._extract_nutrients_many_producers()
    #     except NoSuchElementException:
    #         logger.warning("Нет данных КБЖУ")
    #         return {}
