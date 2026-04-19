from aiogram import Router
from aiogram.types import CallbackQuery
from aiogram.fsm.context import FSMContext
from keyboards.pagination_kb import pagination_kb
from services.product_service import ProductService
from utils import format_products_text

service = ProductService()
router = Router()


@router.callback_query(lambda c: c.data and c.data.startswith("page_"))
async def pagination_handler(callback: CallbackQuery, state: FSMContext):
    page_num = int(callback.data.split("_")[1])

    data = await state.get_data()
    filters = data.get("filters_snapshot", {})
    active_ids = data.get("active_ids")

    try:
        page = await service.search_products(
            page=page_num,
            delivery_service_ids=active_ids or None,
            **filters,
        )
    except Exception as e:
        print(e)
        await callback.answer("Ошибка при обращении к серверу, попробуйте позже")
        return

    if not page.items:
        await callback.answer("Нет продуктов для отображения")
        return

    text = format_products_text(page.items)

    await callback.message.edit_text(
        text=text,
        reply_markup=pagination_kb(page.page, page.total_pages),
    )
    await callback.answer()
