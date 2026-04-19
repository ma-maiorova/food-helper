# Примеры вызова API (curl)

Базовый URL: `http://localhost:8080` (порт можно изменить через конфиг или переменные окружения).

---

## Health

```bash
curl -s http://localhost:8080/health
```

Пример ответа:
```json
{"status":"OK","timestamp":"2026-02-21T12:49:40.178665Z"}
```

---

## Список служб доставки

**Все службы:**
```bash
curl -s http://localhost:8080/api/v1/delivery-services
```

**Только активные:**
```bash
curl -s "http://localhost:8080/api/v1/delivery-services?active=true"
```

Пример ответа (Яндекс Еда и Вкусвилл):
```json
[
  {"id":1,"code":"YANDEX_EATS","name":"Яндекс Еда","siteUrl":"https://eda.yandex.ru","logoUrl":null,"active":true},
  {"id":2,"code":"VKUSVILL","name":"Вкусвилл","siteUrl":"https://vkusvill.ru","logoUrl":null,"active":true}
]
```

---

## Поиск продуктов

Все параметры необязательные. Параметр **size** задаёт количество продуктов на странице (1–100).

**Первая страница (по умолчанию, size=20):**
```bash
curl -s http://localhost:8080/api/v1/products
```

**Пагинация (страница 0, в ответе 2 продукта):**
```bash
curl -s "http://localhost:8080/api/v1/products?page=0&size=2"
```

**Поиск по названию:**
```bash
curl -s "http://localhost:8080/api/v1/products?q=гречка"
```

**Фильтр по службам доставки (ID через запятую):**
```bash
curl -s "http://localhost:8080/api/v1/products?deliveryServiceIds=1,2"
```

**Сортировка (название по возрастанию):**
```bash
curl -s "http://localhost:8080/api/v1/products?sort=name,asc"
```

**Сортировка по цене по убыванию:**
```bash
curl -s "http://localhost:8080/api/v1/products?sort=price,desc"
```

**Фильтр по калориям (мин/макс):**
```bash
curl -s "http://localhost:8080/api/v1/products?minCalories=100&maxCalories=200"
```

**Комбинация: поиск + пагинация + сортировка:**
```bash
curl -s "http://localhost:8080/api/v1/products?q=куриное&page=0&size=5&sort=price,asc"
```

Пример ответа (фрагмент):
```json
{
  "items": [
    {
      "id": 1,
      "name": "Куриное филе на гриле",
      "url": "https://example.com/product/1",
      "price": 350,
      "currency": "RUB",
      "deliveryService": {"id":1,"code":"YANDEX_EATS","name":"Яндекс Еда","siteUrl":"https://eda.yandex.ru","logoUrl":null,"active":true},
      "variants": [
        {
          "id": 1,
          "manufacturer": "Производитель А",
          "composition": "Куриное филе, специи",
          "weight": 200,
          "nutrients": {"calories":150,"protein":25.0,"fat":4.0,"carbs":0.0}
        }
      ]
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 3,
  "totalPages": 1
}
```

---

## Ошибки (примеры)

**Неверный параметр (size > 100) — 400:**
```bash
curl -s "http://localhost:8080/api/v1/products?size=200"
```

Ответ в формате ErrorResponse:
```json
{
  "requestId": "...",
  "code": "VALIDATION_ERROR",
  "message": "size must be between 1 and 100",
  "status": 400,
  "path": "/api/v1/products",
  "timestamp": "...",
  "details": ["size must be between 1 and 100"],
  "fieldErrors": [{"field": "size", "message": "size must be between 1 and 100"}]
}
```

**Корреляция по requestId:** передать свой ID в заголовке:
```bash
curl -s -H "X-Request-Id: my-debug-123" "http://localhost:8080/api/v1/products?size=200"
```

---

## Swagger UI

Интерактивная документация и проверка запросов: **http://localhost:8080/swagger**

В Swagger отображаются все параметры эндпоинтов; обязательных параметров нет — все query-параметры необязательные.
