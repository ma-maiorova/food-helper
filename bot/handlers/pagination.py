from aiogram import Router
from aiogram.types import CallbackQuery
from aiogram.fsm.context import FSMContext
from keyboards.pagination_kb import pagination_kb
from utils import calculate_pagination, format_products_text

router = Router()


@router.callback_query(lambda c: c.data and c.data.startswith("page_"))
async def pagination_handler(callback: CallbackQuery, state: FSMContext):
    page = int(callback.data.split("_")[1])

    data = await state.get_data()
    products = data.get("filtered_products", [])

    if not products:
        await callback.answer("Нет продуктов для отображения")
        return

    start, end, total_pages, current_page = calculate_pagination(
        products, page)
    text = format_products_text(products, start, end)

    await callback.message.edit_text(
        text=text,
        reply_markup=pagination_kb(current_page, total_pages)
    )
    await callback.answer()
