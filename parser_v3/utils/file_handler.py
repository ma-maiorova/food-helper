from dataclasses import asdict
from typing import Iterable
from pathlib import Path
import json
import csv
import pandas as pd
import logging

from models.product_item import Nutrients, ProductVariant, ProductItem


def save_data(data, filename, format='csv'):
    if format == 'json':
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
    elif format == 'csv':
        df = pd.DataFrame(data, columns=['url'])
        df.to_csv(filename, index=False, encoding="utf-8")
    elif format == 'xlsx':
        df = pd.DataFrame(data)
        df.to_excel(filename, index=False)


def load_data(filename, format="csv") -> list[str]:
    if format == 'csv':
        df = pd.read_csv(filename)
        return df['url'].values


def save_products(products: Iterable, filepath: str, file_format: str = "csv"):
    filepath = Path(filepath)
    file_format = file_format or filepath.suffix.replace(".", "").lower()

    if not file_format:
        raise ValueError(
            "Нужно указать расширение файла или параметр file_format")
    print(file_format)
    if file_format == "json":
        _save_json(products, filepath.with_suffix(".json"))
    elif file_format == "csv":
        _save_csv(products, filepath.with_suffix(".csv"))
    elif file_format == "xlsx":
        _save_xlsx(products, filepath.with_suffix(".xlsx"))
    else:
        raise ValueError(f"Неподдерживаемый формат: {file_format}")


def _save_json(data, filepath: Path):
    with filepath.open("w", encoding="utf-8") as f:
        json.dump([asdict(p) for p in data], f, ensure_ascii=False, indent=4)


def _save_csv(data, filepath: Path):
    """Сохранение в CSV."""
    rows = []
    for item in data:
        for variant in item.variants or [None]:
            rows.append({
                "name": item.name,
                "price": item.price,
                "url": item.url,
                "manufacturer": getattr(variant, "manufacturer", None),
                "composition": getattr(variant, "composition", None),
                "calories": getattr(variant.nutrients, "calories", None) if variant else None,
                "protein": getattr(variant.nutrients, "protein", None) if variant else None,
                "fat": getattr(variant.nutrients, "fat", None) if variant else None,
                "carbs": getattr(variant.nutrients, "carbs", None) if variant else None,
            })

    with filepath.open("w", newline="", encoding="utf-8") as file:
        writer = csv.DictWriter(file, fieldnames=rows[0].keys())
        writer.writeheader()
        writer.writerows(rows)


def _save_xlsx(data, filepath: Path):
    if pd is None:
        raise ImportError(
            "Для экспорта в XLSX установи pandas: pip install pandas openpyxl")

    rows = []
    for item in data:
        for variant in item.variants or [None]:
            rows.append({
                "name": item.name,
                "price": item.price,
                "url": item.url,
                "manufacturer": getattr(variant, "manufacturer", None),
                "composition": getattr(variant, "composition", None),
                "calories": getattr(variant.nutrients, "calories", None) if variant else None,
                "protein": getattr(variant.nutrients, "protein", None) if variant else None,
                "fat": getattr(variant.nutrients, "fat", None) if variant else None,
                "carbs": getattr(variant.nutrients, "carbs", None) if variant else None,
            })

    df = pd.DataFrame(rows)
    df.to_excel(filepath, index=False)


def load_products(filepath: str) -> list[ProductItem]:
    """
    Загружает список ProductItem из CSV или JSON, сохранённого функцией save_products.

    CSV-формат (flat, один variant на строку):
        name, price, url, manufacturer, composition,
        calories, protein, fat, carbs

    JSON-формат: список объектов, как возвращает dataclasses.asdict(ProductItem).
    """
    filepath = Path(filepath)
    suffix = filepath.suffix.lower()

    if suffix == ".json":
        return _load_products_json(filepath)
    elif suffix in (".csv",):
        return _load_products_csv(filepath)
    elif suffix in (".xlsx", ".xlsm"):
        return _load_products_csv(filepath, excel=True)
    else:
        raise ValueError(f"Неподдерживаемый формат файла: {suffix}")


def _load_products_json(filepath: Path) -> list[ProductItem]:
    with filepath.open(encoding="utf-8") as f:
        raw = json.load(f)

    products = []
    for item in raw:
        variants = []
        for v in item.get("variants") or []:
            n = v.get("nutrients") or {}
            nutrients = Nutrients(
                calories=n.get("calories"),
                protein=n.get("protein"),
                fat=n.get("fat"),
                carbs=n.get("carbs"),
            )
            variants.append(ProductVariant(
                nutrients=nutrients,
                manufacturer=v.get("manufacturer"),
                composition=v.get("composition"),
                weight=v.get("weight"),
            ))
        products.append(ProductItem(
            name=item["name"],
            url=item["url"],
            price=item["price"],
            variants=variants,
            shop=item.get("shop"),
        ))
    return products


def _load_products_csv(filepath: Path, excel: bool = False) -> list[ProductItem]:
    """
    CSV сохраняется в flat-формате (один вариант на строку).
    Восстанавливаем группировку по (name, url, price).
    """
    if excel:
        df = pd.read_excel(filepath)
    else:
        df = pd.read_csv(filepath)

    # Заменяем NaN на None
    df = df.where(pd.notnull(df), None)

    # Группируем строки по ключу продукта
    groups: dict[tuple, ProductItem] = {}
    for _, row in df.iterrows():
        key = (row["url"], row["name"], row["price"])
        if key not in groups:
            groups[key] = ProductItem(
                name=row["name"],
                url=row["url"],
                price=int(row["price"]) if row["price"] is not None else 0,
                variants=[],
            )

        nutrients = Nutrients(
            calories=int(row["calories"]) if row.get(
                "calories") is not None else None,
            protein=float(row["protein"]) if row.get(
                "protein") is not None else None,
            fat=float(row["fat"]) if row.get("fat") is not None else None,
            carbs=float(row["carbs"]) if row.get(
                "carbs") is not None else None,
        )
        groups[key].variants.append(ProductVariant(
            nutrients=nutrients,
            manufacturer=row.get("manufacturer"),
            composition=row.get("composition"),
        ))

    return list(groups.values())
