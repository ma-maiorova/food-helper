from collections import defaultdict
import csv
import pandas as pd
from services.models import ProductItem, ProductVariant, Nutrients


class ProductService:
    """Работа с данными, когда придет время заменим на API-запросы."""

    def __init__(self, source_path: str):
        self.source_path = source_path
        self.products = self._load(source_path)

    def _load(self, path: str) -> list[ProductItem]:
        """Загружает продукты из CSV и группирует варианты по url."""

        products_map = defaultdict(list)

        with open(path, encoding="utf-8") as file:
            reader = csv.DictReader(file)

            for row in reader:
                url = row["url"].strip()
                name = row["name"].strip()
                price = int(row["price"])

                nutrients = Nutrients(
                    calories=self._to_num(row.get("calories")),
                    protein=self._to_num(row.get("protein")),
                    fat=self._to_num(row.get("fat")),
                    carbs=self._to_num(row.get("carbs")),
                )

                variant = ProductVariant(
                    nutrients=nutrients,
                    manufacturer=row.get("manufacturer") or None,
                    composition=row.get("composition") or None,
                )

                products_map[url].append((name, price, variant))

        products = []
        for url, entries in products_map.items():
            name = entries[0][0]
            price = entries[0][1]
            variants = [v for (_, _, v) in entries]

            products.append(ProductItem(
                name=name,
                url=url,
                price=price,
                variants=variants
            ))

        return products

    def _to_num(self, value):
        """Переводит текст во float или int / возвращает None."""
        if not value or value.strip() == "":
            return None
        try:
            num = float(value.replace(",", "."))
            return int(num) if num.is_integer() else num
        except ValueError:
            return None

    def filter_products(
        self,
        calories: tuple[int, int] | None = None,
        protein: tuple[int, int] | None = None,
        fat: tuple[int, int] | None = None,
        carbs: tuple[int, int] | None = None,
    ) -> list[ProductItem]:

        def check(val, rng):
            return (rng is None) or (val and rng[0] <= val <= rng[1])

        result = []
        for p in self.products:
            for v in p.variants:
                if not check(v.nutrients.calories, calories):
                    continue
                if not check(v.nutrients.protein, protein):
                    continue
                if not check(v.nutrients.fat, fat):
                    continue
                if not check(v.nutrients.carbs, carbs):
                    continue
                result.append(p)
                break
        return result
