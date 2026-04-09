from aiogram import Router
from keyboards.pagination_kb import pagination_kb
from services.models import ProductPage
from utils import format_products_text

router = Router()


async def show_products(msg, page: ProductPage):
    """Отображает страницу продуктов с клавиатурой пагинации."""
    text = format_products_text(page.items)
    await msg.answer(
        text,
        reply_markup=pagination_kb(page.page, page.total_pages),
    )
