from aiogram import Router, types, F
from keyboards.filters_kb import get_filters_kb

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
