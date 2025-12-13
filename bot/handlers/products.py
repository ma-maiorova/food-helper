from aiogram import Router
from keyboards.pagination_kb import pagination_kb
from keyboards.filters_kb import get_filters_kb
from utils import calculate_pagination, format_products_text

router = Router()


async def show_products(msg, products, page=0):
    start, end, total_pages, current_page = calculate_pagination(
        products, page)
    text = format_products_text(products, start, end)

    await msg.answer(text, reply_markup=pagination_kb(current_page, total_pages))
