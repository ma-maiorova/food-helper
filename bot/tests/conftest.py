import pytest
from unittest.mock import AsyncMock
from services.models import DeliveryService, Nutrients, ProductItem, ProductPage, ProductVariant


@pytest.fixture
def msg():
    return AsyncMock()


@pytest.fixture
def cb():
    c = AsyncMock()
    c.message = AsyncMock()
    return c


@pytest.fixture
def state():
    s = AsyncMock()
    s.get_data.return_value = {}
    return s


@pytest.fixture
def page():
    ds = DeliveryService(id=1, code="svc", name="Service")
    nutrients = Nutrients(calories=200, protein=20, fat=5, carbs=30)
    item = ProductItem(
        id=1, name="Dish", url="http://x.com", price=150, currency="RUB",
        delivery_service=ds,
        variants=[ProductVariant(id=1, nutrients=nutrients)],
    )
    return ProductPage(items=[item], page=0, size=2, total_elements=1, total_pages=3)
