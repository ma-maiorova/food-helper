from aiogram.types import InlineKeyboardButton, InlineKeyboardMarkup


def get_filters_kb(current_filters: dict) -> InlineKeyboardMarkup:
    def format_range(key):
        if key in current_filters:
            return f"{current_filters[key][0]}-{current_filters[key][1]}"
        return "–"

    kb = InlineKeyboardMarkup(inline_keyboard=[
        [
            InlineKeyboardButton(
                text=f"Калории: {format_range('calories')}", callback_data="set_calories"),
            InlineKeyboardButton(
                text=f"Белки: {format_range('protein')}", callback_data="set_protein"),
        ],
        [
            InlineKeyboardButton(
                text=f"Жиры: {format_range('fat')}", callback_data="set_fat"),
            InlineKeyboardButton(
                text=f"Углеводы: {format_range('carbs')}", callback_data="set_carbs"),
        ],
        [
            InlineKeyboardButton(
                text="🔍 Поиск", callback_data="search_products")
        ]
    ])
    return kb
