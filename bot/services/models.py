from dataclasses import dataclass, field


@dataclass
class Nutrients:
    """Хранит информацию о КБЖУ на 100г продукта."""
    calories: int | None = None
    protein: float | None = None
    fat: float | None = None
    carbs: float | None = None


@dataclass
class DeliveryService:
    """Служба доставки."""
    id: int
    code: str
    name: str
    site_url: str | None = None
    logo_url: str | None = None
    active: bool = True


@dataclass
class ProductVariant:
    """Хранит информацию о продукте по конкретному производителю."""
    id: int
    nutrients: Nutrients
    manufacturer: str | None = None
    composition: str | None = None
    weight: int | None = None


@dataclass
class ProductItem:
    """Хранит информацию о продукте."""
    id: int
    name: str
    url: str
    price: int
    currency: str
    delivery_service: DeliveryService
    variants: list[ProductVariant] = field(default_factory=list)


@dataclass
class ProductPage:
    """Пагинированный список продуктов."""
    items: list[ProductItem]
    page: int
    size: int
    total_elements: int
    total_pages: int
