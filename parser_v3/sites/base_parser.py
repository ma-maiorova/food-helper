from abc import ABC, abstractmethod


class BaseParser(ABC):
    """Базовый класс парсера."""

    def __init__(self, base_url, delivery_address=None):
        self.base_url = base_url
        self.delivery_address = delivery_address

    @abstractmethod
    def get_product_links(self):
        """Собирает ссылки на все продукты."""
        pass

    @abstractmethod
    def parse_product(self, url):
        """Парсит полезные данные со страницы продукта."""
        pass
