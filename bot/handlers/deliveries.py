from aiogram import F, Router
from aiogram.types import CallbackQuery
from aiogram.fsm.context import FSMContext
from handlers.start import cmd_filters
from keyboards.deliveries_kb import get_deliveries_kb
from services.product_service import ProductService

service = ProductService()
router = Router()


async def load_deliveries_state(state: FSMContext) -> dict:
    """Возвращает словарь deliveries из стейта.
    Структура: {code: {"id": int, "name": str, "excluded": 0|1}}
    Если пустой — подгружает из API."""
    data = await state.get_data()
    deliveries = data.get("deliveries", {})

    if not deliveries:
        try:
            services = await service.get_delivery_services()
            deliveries = {
                s.code: {"id": s.id, "name": s.name, "excluded": 0}
                for s in services
            }
            await state.update_data(deliveries=deliveries)
        except Exception as e:
            print(e)

    return deliveries


@router.callback_query(F.data == "/filters")
async def filters_handler(callback: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    filters = data.get("filters", {})
    await cmd_filters(callback.message, filters)


@router.callback_query(F.data.startswith("choice_"))
async def set_delivery(callback: CallbackQuery, state: FSMContext):
    await callback.answer()

    deliveries = await load_deliveries_state(state)
    name = callback.data.removeprefix("choice_")

    if name in deliveries:
        deliveries[name]["excluded"] ^= 1
        await state.update_data(deliveries=deliveries)

    await callback.message.edit_reply_markup(
        reply_markup=get_deliveries_kb(deliveries)
    )


@router.callback_query(F.data == "select_all")
async def select_all(callback: CallbackQuery, state: FSMContext):
    deliveries = await load_deliveries_state(state)
    for v in deliveries.values():
        v["excluded"] = 0

    await state.update_data(deliveries=deliveries)
    await callback.message.edit_reply_markup(reply_markup=get_deliveries_kb(deliveries))
    await callback.answer()


@router.callback_query(F.data == "clear_all")
async def clear_all(callback: CallbackQuery, state: FSMContext):
    deliveries = await load_deliveries_state(state)
    for v in deliveries.values():
        v["excluded"] = 1

    await state.update_data(deliveries=deliveries)
    await callback.message.edit_reply_markup(reply_markup=get_deliveries_kb(deliveries))
    await callback.answer()
