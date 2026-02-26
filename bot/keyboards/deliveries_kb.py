from aiogram.types import InlineKeyboardButton, InlineKeyboardMarkup


def get_deliveries_kb(current_deliveries: dict) -> InlineKeyboardMarkup:
    CHOICE_MAP = ["✅", " "]

    kb = InlineKeyboardMarkup(inline_keyboard=[
        [
            InlineKeyboardButton(
                text=f"Вкусвилл {CHOICE_MAP[current_deliveries.get('vkusvill', 0)]}", callback_data="choice_vkusvill",
            ),
            InlineKeyboardButton(
                text=f"Яндекс лавка {CHOICE_MAP[current_deliveries.get('lavka', 0)]}", callback_data="choice_lavka",
            )
        ],
        [
            InlineKeyboardButton(
                text="Выбрать все", callback_data="select_all",
            ),
            InlineKeyboardButton(
                text="🗑️ Удалить все", callback_data="clear_all",
            )
        ],
        [
            InlineKeyboardButton(
                text="Настроить фильтры КБЖУ", callback_data="/filters",
            )
        ],
        [
            InlineKeyboardButton(
                text="🔍 Поиск", callback_data="search_products",
            )
        ],
    ])

    return kb
