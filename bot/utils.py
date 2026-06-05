from services.models import ProductItem, ProductVariant


def _fmt_variant(v: ProductVariant) -> str:
    weight_str = f"⚖️ {v.weight} г  " if v.weight else ""
    return (
        f"{weight_str}"
        f"К: {v.nutrients.calories}  "
        f"Б: {v.nutrients.protein}  "
        f"Ж: {v.nutrients.fat}  "
        f"У: {v.nutrients.carbs}"
    )


def format_products_text(products: list[ProductItem]) -> str:
    """Форматирует текст для отображения списка продуктов."""

    text = "\n\n\n".join([
        f"<b>{item.name}</b>\n"
        f"💰 Цена: {item.price}₽\n"
        f"🚚 {item.delivery_service.name}\n"
        f"<b>КБЖУ (на 100 г)</b>\n"
        + (
            _fmt_variant(item.variants[0])
            if len(item.variants) == 1
            else "\n".join([
                f"{i}) {variant.manufacturer or 'Без указания производителя'}\n"
                + _fmt_variant(variant)
                for i, variant in enumerate(item.variants, 1)
            ])
        ) +
        f"\n\n🔗 {item.url}"

        for item in products
    ])

    return text
