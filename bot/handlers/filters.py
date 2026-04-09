from aiogram.fsm.context import FSMContext
from aiogram import F, Router
from aiogram.types import CallbackQuery
from handlers.products import show_products
from keyboards.filters_kb import get_filters_kb
from services.product_service import ProductService
from states.filter_states import FilterStates

from handlers.start import cmd_delivery

service = ProductService()
router = Router()

NUTRIENT_MAP = {
    "set_calories": "calories",
    "set_protein": "protein",
    "set_fat": "fat",
    "set_carbs": "carbs"
}


@router.callback_query(lambda c: c.data in NUTRIENT_MAP.keys())
async def set_nutrient_handler(callback: CallbackQuery, state: FSMContext):
    nutrient = NUTRIENT_MAP[callback.data]
    await state.update_data(current_nutrient=nutrient)

    await callback.message.answer(
        f"Введите диапазон для {nutrient} в формате min-max, например 100-400:"
    )
    await state.set_state(FilterStates.waiting_for_input)
    await callback.answer()


@router.message(FilterStates.waiting_for_input)
async def input_range_handler(message, state: FSMContext):
    data = await state.get_data()
    nutrient = data.get("current_nutrient")
    if not nutrient:
        await message.answer("Что-то пошло не так, повторите выбор нутриента.")
        return

    try:
        min_val, max_val = map(int, message.text.split("-"))
        filters = data.get("filters", {})
        filters[nutrient] = (min_val, max_val)
        await state.update_data(filters=filters)
    except Exception as e:
        print(e)
        await message.answer("Неверный формат! Введите диапазон min-max, например 100-400")
        return

    await message.answer(
        "Выберите следующий фильтр или нажмите поиск:",
        reply_markup=get_filters_kb(filters),
    )


@router.callback_query(lambda c: c.data == "search_products")
async def search_products_handler(callback: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    filters = data.get("filters", {})
    deliveries = data.get("deliveries", {})

    # Собираем ID только включённых служб
    active_ids = [v["id"] for v in deliveries.values() if not v["excluded"]]

    try:
        page = await service.search_products(
            page=0,
            delivery_service_ids=active_ids or None,
            **filters,
        )
    except Exception as e:
        print(e)
        await callback.answer("Ошибка при обращении к серверу, попробуйте позже")
        return

    if page.total_elements == 0:
        await callback.answer("Продуктов не найдено, измените диапазоны фильтрации")
    else:
        await state.update_data(filters_snapshot=filters, active_ids=active_ids)
        await show_products(callback.message, page)
        await callback.answer()


@router.callback_query(F.data == "clear_filters")
async def clear_filters_handler(callback: CallbackQuery, state: FSMContext):
    await state.update_data(filters={})

    await callback.message.edit_reply_markup(
        reply_markup=get_filters_kb({})
    )

    await callback.answer()


@router.callback_query(F.data == "/delivery")
async def delivery_handler(callback: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    await cmd_delivery(callback.message, state, deliveries=data.get("deliveries"))
