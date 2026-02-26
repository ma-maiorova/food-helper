from aiogram import F, Router
from aiogram.types import CallbackQuery
from aiogram.fsm.context import FSMContext
from handlers.start import cmd_filters
from keyboards.deliveries_kb import get_deliveries_kb

router = Router()


@router.callback_query(F.data == "/filters")
async def filters_handler(callback: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    filters = data.get("filters", {})
    await cmd_filters(callback.message, filters)


@router.callback_query(F.data.startswith("choice_"))
async def set_delivery(callback: CallbackQuery, state: FSMContext):
    await callback.answer()

    data = await state.get_data()
    deliveries = data.get("deliveries", {})

    name = callback.data.removeprefix("choice_")
    deliveries[name] = deliveries.get(name, 0) ^ 1

    await state.update_data(deliveries=deliveries)

    await callback.message.edit_reply_markup(
        reply_markup=get_deliveries_kb(deliveries)
    )


@router.callback_query(F.data == "select_all")
async def select_all(callback: CallbackQuery, state: FSMContext):
    current_deliveries = {
        "vkusvill": 0,
        "lavka": 0,
    }

    await state.update_data(deliveries=current_deliveries)

    await callback.message.edit_reply_markup(
        reply_markup=get_deliveries_kb(current_deliveries)
    )

    await callback.answer()


@router.callback_query(F.data == "clear_all")
async def clear_all(callback: CallbackQuery, state: FSMContext):
    current_deliveries = {
        "vkusvill": 1,
        "lavka": 1,
    }

    await state.update_data(deliveries=current_deliveries)

    await callback.message.edit_reply_markup(
        reply_markup=get_deliveries_kb(current_deliveries)
    )

    await callback.answer()
