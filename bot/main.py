from aiogram import Bot, Dispatcher
from aiogram.enums import ParseMode
from aiogram.types import BotCommand
from aiogram.client.default import DefaultBotProperties
import asyncio

from handlers import start, deliveries, filters, products, pagination

from config import settings
from logging_config import setup_logging

import logging


async def set_commands(bot: Bot):
    await bot.set_my_commands([
        BotCommand(command="start", description="Запуск"),
        BotCommand(command="filters", description="Фильтры КБЖУ"),
        BotCommand(command="delivery", description="Выбор сервиса доставки"),
        BotCommand(command="help", description="Помощь"),
    ])


async def main():
    setup_logging()
    logger = logging.getLogger(__name__)

    bot = Bot(token=settings.bot_token,
              default=DefaultBotProperties(parse_mode=ParseMode.HTML))

    dp = Dispatcher()

    dp.include_router(start.router)
    dp.include_router(filters.router)
    dp.include_router(products.router)
    dp.include_router(pagination.router)
    dp.include_router(deliveries.router)

    await set_commands(bot)

    logger.info("Bot started!")

    await dp.start_polling(bot)


if __name__ == "__main__":
    asyncio.run(main())
