from unittest.mock import AsyncMock, patch
from handlers.pagination import pagination_handler


async def test_next_page(cb, state, page):
    cb.data = "page_1"
    page.page = 1
    state.get_data.return_value = {"filters_snapshot": {}, "active_ids": None, "per_dish_snapshot": False}
    with patch("handlers.pagination.service.search_products", new=AsyncMock(return_value=page)):
        await pagination_handler(cb, state)
    cb.message.edit_text.assert_called_once()
