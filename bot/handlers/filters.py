from aiogram.fsm.context import FSMContext
from aiogram import Router
from aiogram.types import CallbackQuery
from handlers.products import show_products
from keyboards.filters_kb import get_filters_kb
from services.product_service import ProductService
from states.filter_states import FilterStates

service = ProductService("data/products.csv")
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
        # await state.clear()
    except Exception as e:
        print(e)
        await message.answer("Неверный формат! Введите диапазон min-max, например 100-400")
        return

    await message.answer("Выберите следующий фильтр или нажмите поиск:",
                         reply_markup=get_filters_kb(filters))


@router.callback_query(lambda c: c.data == "search_products")
async def search_products_handler(callback: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    filters = data.get("filters", {})

    products = service.filter_products(**filters)

    if len(products) == 0:
        text = "Продуктов не найдено, измените диапазоны фильтрации"
        await callback.answer(text)
    else:
        await state.update_data(filtered_products=products)
        await show_products(callback.message, products, page=0)
        await callback.answer()
