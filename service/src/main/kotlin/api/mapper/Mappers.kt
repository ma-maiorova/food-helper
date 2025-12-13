package org.example.api.mapper

import org.example.api.dto.*
import org.example.domain.*

fun DeliveryService.toDto(): DeliveryServiceDto =
    DeliveryServiceDto(
        id = id,
        code = code,
        name = name,
        siteUrl = siteUrl,
        logoUrl = logoUrl,
        active = active
    )

fun Nutrients.toDto(): NutrientsDto =
    NutrientsDto(
        calories = calories,
        protein = protein,
        fat = fat,
        carbs = carbs
    )

fun ProductVariant.toDto(): ProductVariantDto =
    ProductVariantDto(
        id = id,
        manufacturer = manufacturer,
        composition = composition,
        weight = weight,
        nutrients = nutrients.toDto()
    )

fun Product.toDto(): ProductDto =
    ProductDto(
        id = id,
        name = name,
        url = url,
        price = price,
        currency = currency,
        deliveryService = deliveryService.toDto(),
        variants = variants.map { it.toDto() }
    )
