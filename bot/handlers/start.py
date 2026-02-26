from aiogram import Router, types, F
from keyboards.filters_kb import get_filters_kb
from keyboards.deliveries_kb import get_deliveries_kb

router = Router()


@router.message(F.text == "/start")
async def cmd_start(msg: types.Message):
    await msg.answer(
        "<b>Привет!</b> Я помогу подобрать еду, отфильтрованную по КБЖУ.\n"
        "Выбери параметр фильтра:",
        reply_markup=get_filters_kb(current_filters={})
    )


@router.message(F.text == "/filters")
async def cmd_filters(msg: types.Message):
    await msg.answer("Выберите параметры фильтра:",
                     reply_markup=get_filters_kb({}))


@router.message(F.text == "/delivery")
async def cmd_delivery(msg: types.Message):
    await msg.answer("Выберите сервис с продуктами готовой еды:",
                     reply_markup=get_deliveries_kb({}))
