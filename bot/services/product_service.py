import aiohttp
from config import settings
from services.models import (
    DeliveryService, Nutrients, ProductItem,
    ProductPage, ProductVariant,
)


def _parse_delivery_service(data: dict) -> DeliveryService:
    return DeliveryService(
        id=data["id"],
        code=data["code"],
        name=data["name"],
        site_url=data.get("siteUrl"),
        logo_url=data.get("logoUrl"),
        active=data.get("active", True),
    )


def _parse_nutrients(data: dict) -> Nutrients:
    return Nutrients(
        calories=data.get("calories"),
        protein=data.get("protein"),
        fat=data.get("fat"),
        carbs=data.get("carbs"),
    )


def _parse_variant(data: dict) -> ProductVariant:
    return ProductVariant(
        id=data["id"],
        nutrients=_parse_nutrients(data["nutrients"]),
        manufacturer=data.get("manufacturer"),
        composition=data.get("composition"),
        weight=data.get("weight"),
    )


def _parse_product(data: dict) -> ProductItem:
    return ProductItem(
        id=data["id"],
        name=data["name"],
        url=data["url"],
        price=data["price"],
        currency=data.get("currency", "RUB"),
        delivery_service=_parse_delivery_service(data["deliveryService"]),
        variants=[_parse_variant(v) for v in data.get("variants") or []],
    )


class ProductService:
    """Работа с данными через REST API."""

    BASE_URL = settings.api_base_url

    async def get_delivery_services(self) -> list[DeliveryService]:
        """Возвращает список всех активных служб доставки."""
        url = f"{self.BASE_URL}/api/v1/delivery-services"
        async with aiohttp.ClientSession() as session:
            async with session.get(url, params={"active": "true"}) as response:
                response.raise_for_status()
                data = await response.json()
                return [_parse_delivery_service(item) for item in data]

    async def search_products(
        self,
        page: int = 0,
        size: int = 2,
        delivery_service_ids: list[int] | None = None,
        calories: tuple[int, int] | None = None,
        protein: tuple[int, int] | None = None,
        fat: tuple[int, int] | None = None,
        carbs: tuple[int, int] | None = None,
    ) -> ProductPage:
        """Ищет продукты с фильтрацией и пагинацией на стороне бэкенда."""
        url = f"{self.BASE_URL}/api/v1/products"

        params: dict = {"page": page, "size": size}

        # API принимает deliveryServiceIds как строку вида "1,2,3"
        if delivery_service_ids:
            params["deliveryServiceIds"] = ",".join(
                str(i) for i in delivery_service_ids)

        for name, val in [
            ("minCalories", calories[0] if calories else None),
            ("maxCalories", calories[1] if calories else None),
            ("minProtein",  protein[0] if protein else None),
            ("maxProtein",  protein[1] if protein else None),
            ("minFat",      fat[0] if fat else None),
            ("maxFat",      fat[1] if fat else None),
            ("minCarbs",    carbs[0] if carbs else None),
            ("maxCarbs",    carbs[1] if carbs else None),
        ]:
            if val is not None:
                params[name] = val

        async with aiohttp.ClientSession() as session:
            async with session.get(url, params=params) as response:
                response.raise_for_status()
                data = await response.json()

        return ProductPage(
            items=[_parse_product(p) for p in data["items"]],
            page=data["page"],
            size=data["size"],
            total_elements=data["totalElements"],
            total_pages=data["totalPages"],
        )
