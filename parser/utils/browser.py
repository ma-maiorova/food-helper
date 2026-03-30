# utils/browser.py
import undetected_chromedriver as uc


def get_driver(logger, headless: bool = False):
    """
    Создает и возвращает экземпляр Chrome через undetected_chromedriver.
    """
    logger.info("Создаю драйвер...")
    options = uc.ChromeOptions()

    if headless:
        options.add_argument("--headless=new")
    options.add_argument(
        "--disable-blink-features=AutomationControlled")
    options.add_argument("--disable-gpu")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-extensions")
    options.add_argument("--disable-infobars")
    options.add_argument("--lang=ru-RU")

    driver = uc.Chrome(options=options, version_main=145)
    driver.set_page_load_timeout(30)
    logger.info("Драйвер успешно работает")
    return driver
