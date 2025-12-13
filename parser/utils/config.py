OUTPUT_PATH = "output.csv"
BASE_LINK = "https://rtw.vkusvill.ru/goods/"
data = {
    "sections": {
        "Новинки": {
            "url": BASE_LINK + "novinki/",
            "enabled": False
        },
        "Хиты": {
            "url": BASE_LINK + "khity/",
            "enabled": False
        },
        "Готовая еда": {
            "url": BASE_LINK + "gotovaya-eda/",
            "enabled": True
        },
        "Овощи, фрукты, ягоды, зелень": {
            "url": BASE_LINK + "ovoshchi-frukty-yagody-zelen/",
            "enabled": False
        },
        "Молочные продукты, яйцо": {
            "url": BASE_LINK + "molochnye-produkty-yaytso/",
            "enabled": False
        },
        "Сладости и десерты": {
            "url": BASE_LINK + "sladosti-i-deserty/",
            "enabled": False
        },
        "Мясо, птица": {
            "url": BASE_LINK + "myaso-ptitsa/",
            "enabled": False
        },
        "Хлеб и выпечка": {
            "url": BASE_LINK + "khleb-i-vypechka/",
            "enabled": False
        },
        "Рыба, икра и морепродукты": {
            "url": BASE_LINK + "ryba-ikra-i-moreprodukty/",
            "enabled": False
        },
        "Сыры": {
            "url": BASE_LINK + "syry/",
            "enabled": False
        },
        "Колбасы и мясные деликатесы": {
            "url": BASE_LINK + "kolbasy-i-myasnye-delikatesy/",
            "enabled": False
        },
        "Супермаркет": {
            "url": BASE_LINK + "supermarket/",
            "enabled": False
        },
        "Замороженные продукты": {
            "url": BASE_LINK + "zamorozhennye-produkty/",
            "enabled": False
        },
        "Напитки": {
            "url": BASE_LINK + "napitki/",
            "enabled": False
        },
        "Мороженое": {
            "url": BASE_LINK + "morozhenoe/",
            "enabled": False
        },
        "Детское питание, гигиена и развитие": {
            "url": BASE_LINK + "detskoe-pitanie-gigiena-i-razvitie/",
            "enabled": False
        },
        "Орехи, чипсы и снеки": {
            "url": BASE_LINK + "orekhi-chipsy-i-sneki/",
            "enabled": False
        },
        "Консервация": {
            "url": BASE_LINK + "konservatsiya/",
            "enabled": False
        },
        "Крупы, макароны, мука": {
            "url": BASE_LINK + "krupy-makarony-muka/",
            "enabled": False
        },
        "Масла, соусы, сахар и соль": {
            "url": BASE_LINK + "masla-sousy-sakhar-i-sol/",
            "enabled": False
        },
        "Чай и кофе": {
            "url": BASE_LINK + "chay-i-kofe/",
            "enabled": False
        },
        "Вегетарианское и постное": {
            "url": BASE_LINK + "vegetarianskoe-i-postnoe/",
            "enabled": False
        },
        "Особое питание": {
            "url": BASE_LINK + "osoboe-pitanie/",
            "enabled": False
        }
    }
}


def get_section_links(data: dict) -> list:
    links = []
    sections = data["sections"]
    for section in sections:
        cur_data = sections[section]
        if cur_data["enabled"]:
            links.append(cur_data["url"])
    return links
