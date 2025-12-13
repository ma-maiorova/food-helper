from aiogram import Bot, Dispatcher
from aiogram.enums import ParseMode
from aiogram.types import BotCommand
from aiogram.client.default import DefaultBotProperties
import asyncio

from config import TOKEN
from handlers.start import router as start_router
from handlers.filters import router as filters_router
from handlers.products import router as products_router
from handlers.pagination import router as pagination_router

bot = Bot(
    token=TOKEN,
    default=DefaultBotProperties(parse_mode=ParseMode.HTML)
)
dp = Dispatcher()

dp.include_router(start_router)
dp.include_router(filters_router)
dp.include_router(products_router)
dp.include_router(pagination_router)


async def on_startup(bot: Bot):
    await bot.set_my_commands([
        BotCommand(command="start", description="Запуск"),
        BotCommand(command="filters", description="Фильтры КБЖУ"),
    ])


async def main():
    await on_startup(bot)
    await dp.start_polling(bot)


if __name__ == "__main__":
    asyncio.run(main())
