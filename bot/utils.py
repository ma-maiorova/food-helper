from services.models import ProductItem


def format_products_text(products: list[ProductItem]) -> str:
    """Форматирует текст для отображения списка продуктов."""

    text = "\n\n\n".join([
        f"<b>{item.name}</b>\n"
        f"💰 Цена: {item.price}₽\n"
        f"🚚 {item.delivery_service.name}\n"
        f"<b>КБЖУ</b>\n"
        + (
            f"К: {item.variants[0].nutrients.calories}\n"
            f"Б: {item.variants[0].nutrients.protein}\n"
            f"Ж: {item.variants[0].nutrients.fat}\n"
            f"У: {item.variants[0].nutrients.carbs}"
            if len(item.variants) == 1

            else "\n".join([
                f"{i}) {variant.manufacturer or 'Без указания производителя'}\n"
                f"К: {variant.nutrients.calories}, "
                f"Б: {variant.nutrients.protein}, "
                f"Ж: {variant.nutrients.fat}, "
                f"У: {variant.nutrients.carbs}"
                for i, variant in enumerate(item.variants, 1)
            ])
        ) +
        f"\n\n🔗 {item.url}"

        for item in products
    ])

    return text
