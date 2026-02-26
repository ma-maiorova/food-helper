from dataclasses import asdict
from typing import Iterable
from pathlib import Path
import json
import csv
import pandas as pd
import logging


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
