from unittest.mock import AsyncMock, patch
from handlers.filters import (
    set_nutrient_handler,
    input_range_handler,
    search_products_handler,
    clear_filters_handler,
    toggle_per_dish_handler,
)
from states.filter_states import FilterStates


async def test_set_nutrient(cb, state):
    cb.data = "set_calories"
    await set_nutrient_handler(cb, state)
    state.set_state.assert_called_once_with(FilterStates.waiting_for_input)


async def test_range_valid(msg, state):
    state.get_data.return_value = {"current_nutrient": "calories", "filters": {}}
    msg.text = "100-400"
    await input_range_handler(msg, state)
    state.update_data.assert_called()
    msg.answer.assert_called_once()


async def test_range_invalid(msg, state):
    state.get_data.return_value = {"current_nutrient": "calories"}
    msg.text = "bad"
    await input_range_handler(msg, state)
    assert "Неверный" in msg.answer.call_args[0][0]


async def test_search_found(cb, state, page):
    state.get_data.return_value = {"filters": {}, "deliveries": {}, "per_dish": False}
    with patch("handlers.filters.service.search_products", new=AsyncMock(return_value=page)):
        await search_products_handler(cb, state)
    cb.message.answer.assert_called_once()


async def test_search_empty(cb, state, page):
    page.total_elements = 0
    page.items = []
    state.get_data.return_value = {"filters": {}, "deliveries": {}, "per_dish": False}
    with patch("handlers.filters.service.search_products", new=AsyncMock(return_value=page)):
        await search_products_handler(cb, state)
    cb.answer.assert_called_with("Продуктов не найдено, измените диапазоны фильтрации")


async def test_clear(cb, state):
    state.get_data.return_value = {"per_dish": False, "filters": {"calories": (100, 400)}}
    await clear_filters_handler(cb, state)
    state.update_data.assert_called_with(filters={})


async def test_toggle_per_dish(cb, state):
    state.get_data.return_value = {"per_dish": False, "filters": {}}
    await toggle_per_dish_handler(cb, state)
    state.update_data.assert_called_with(per_dish=True)
