INSERT INTO delivery_service (code, name, site_url, logo_url)
VALUES
    ('YANDEX_EATS', 'Яндекс Еда', 'https://eda.yandex.ru', NULL),
    ('DELIVERY_CLUB', 'Delivery Club', 'https://www.delivery-club.ru', NULL);

INSERT INTO product (name, url, price, currency, delivery_service_id)
VALUES
    ('Куриное филе на гриле', 'https://example.com/product/1', 350, 'RUB', 1),
    ('Гречка отварная',       'https://example.com/product/2', 150, 'RUB', 1),
    ('Салат Цезарь',          'https://example.com/product/3', 420, 'RUB', 2);

INSERT INTO product_variant (product_id, manufacturer, composition, weight, calories, protein, fat, carbs)
VALUES
    (1, 'Производитель А', 'Куриное филе, специи',                    200, 150, 25.0, 4.0, 0.0),
    (1, 'Производитель Б', 'Куриное филе, специи, масло',             180, 180, 27.0, 7.0, 1.0),
    (2, 'Производитель В', 'Крупа гречневая',                         150, 110, 4.0, 1.0, 22.0),
    (3, 'Производитель Г', 'Курица, салат, сухарики, соус',           250, 210, 12.0, 14.0, 10.0);
