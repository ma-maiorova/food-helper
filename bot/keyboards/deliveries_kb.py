from aiogram.types import InlineKeyboardButton, InlineKeyboardMarkup


def get_deliveries_kb(deliveries: dict) -> InlineKeyboardMarkup:
    """
    deliveries: {code: {"id": int, "name": str, "excluded": 0|1}}
    """
    CHOICE_MAP = ["✅", " "]

    service_buttons = [
        InlineKeyboardButton(
            text=f"{info['name']} {CHOICE_MAP[info['excluded']]}",
            callback_data=f"choice_{code}",
        )
        for code, info in deliveries.items()
    ]

    # Разбиваем кнопки служб по 2 в ряд
    rows = [service_buttons[i:i + 2]
            for i in range(0, len(service_buttons), 2)]

    rows += [
        [
            InlineKeyboardButton(text="Выбрать все",
                                 callback_data="select_all"),
            InlineKeyboardButton(text="🗑️ Удалить все",
                                 callback_data="clear_all"),
        ],
        [
            InlineKeyboardButton(
                text="Настроить фильтры КБЖУ", callback_data="/filters"),
        ],
        [
            InlineKeyboardButton(
                text="🔍 Поиск", callback_data="search_products"),
        ],
    ]

    return InlineKeyboardMarkup(inline_keyboard=rows)
