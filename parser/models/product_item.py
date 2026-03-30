from dataclasses import dataclass, field


@dataclass
class Nutrients:
    """Хранит информацию о КБЖУ на 100г продукта."""
    calories: int | None = None
    protein: float | None = None
    fat: float | None = None
    carbs: float | None = None


@dataclass
class ProductVariant:
    """Хранит информацию о продукте по конкретному производителю."""
    nutrients: Nutrients
    manufacturer: str | None = None
    composition: str | None = None
    weight: int | None = None


@dataclass
class ProductItem:
    """Хранит информацию о продукте с учетом разных производителей.

    Цена в рублях"""
    name: str
    url: str
    price: int
    variants: list[ProductVariant] = field(default_factory=list)
    shop: str = None
