# utils/browser.py
import undetected_chromedriver as uc


def get_driver(logger, headless: bool = True):
    """
    Создает и возвращает экземпляр Chrome через undetected_chromedriver.
    В Docker запускается с виртуальным дисплеем Xvfb (см. entrypoint.sh).
    """
    logger.info("Создаю драйвер...")
    options = uc.ChromeOptions()

    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    options.add_argument("--disable-blink-features=AutomationControlled")
    options.add_argument("--disable-extensions")
    options.add_argument("--disable-infobars")
    options.add_argument("--lang=ru-RU")
    options.add_argument("--window-size=1920,1080")
    options.add_argument("--disable-setuid-sandbox")

    # Явные пути к бинарникам, установленным в Dockerfile
    options.binary_location = "/opt/chrome/chrome"

    driver = uc.Chrome(
        options=options,
        driver_executable_path="/usr/local/bin/chromedriver",
        version_main=145,
    )
    driver.set_page_load_timeout(30)
    logger.info("Драйвер успешно работает")
    return driver
