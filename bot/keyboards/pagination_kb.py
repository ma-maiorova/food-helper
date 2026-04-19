from aiogram.types import InlineKeyboardButton, InlineKeyboardMarkup


def pagination_kb(page: int, total_pages: int) -> InlineKeyboardMarkup:
    buttons = [[
        InlineKeyboardButton(
            text="⬅️", callback_data=f"page_{page-1}"
        ) if page > 0 else InlineKeyboardButton(
            text=" ", callback_data="none"
        ),
        InlineKeyboardButton(
            text=f"{page+1}/{total_pages}", callback_data="none"
        ),
        InlineKeyboardButton(
            text="➡️", callback_data=f"page_{page+1}"
        ) if page < total_pages-1 else InlineKeyboardButton(
            text=" ", callback_data="none"
        ),],
        [
        InlineKeyboardButton(
            text="Настроить фильтры КБЖУ", callback_data="/filters")],
        [InlineKeyboardButton(
            text="Выбрать сервис доставки", callback_data="/delivery")],
    ]
    return InlineKeyboardMarkup(inline_keyboard=buttons)
