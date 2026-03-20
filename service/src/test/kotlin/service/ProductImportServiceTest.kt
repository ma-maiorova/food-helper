package service

import kotlinx.coroutines.runBlocking
import org.example.api.dto.ParserImportItemDto
import org.example.api.dto.ParserImportNutrientsDto
import org.example.api.dto.ParserImportRequest
import org.example.api.dto.ParserImportVariantDto
import org.example.api.errors.ValidationException
import org.example.domain.DeliveryService
import org.example.repository.DeliveryServiceRepository
import org.example.repository.ProductImportRepository
import org.example.service.ProductImportService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProductImportServiceTest {

    // ── Fake repositories ────────────────────────────────────────────────────

    private val knownService = DeliveryService(1L, "VKUSVILL", "ВкусВилл", null, null, true)

    private val fakeDeliveryRepo = object : DeliveryServiceRepository {
        override suspend fun getAll() = listOf(knownService)
        override suspend fun findById(id: Long) = if (id == 1L) knownService else null
        override suspend fun findByCode(code: String) = if (code == "VKUSVILL") knownService else null
        override suspend fun create(code: String, name: String, siteUrl: String?, logoUrl: String?, active: Boolean) =
            DeliveryService(2L, code, name, siteUrl, logoUrl, active)
        override suspend fun update(id: Long, name: String?, siteUrl: String?, logoUrl: String?, active: Boolean?) = null
        override suspend fun delete(id: Long) = false
    }

    private val fakeImportRepo = object : ProductImportRepository {
        override suspend fun upsertProduct(
            deliveryServiceId: Long, name: String, url: String, price: Int, currency: String
        ) = 42L

        override suspend fun replaceVariants(
            productId: Long,
            variants: List<org.example.api.dto.ParserImportVariantDto>
        ) {}
    }

    private fun service(maxItems: Int = 10) =
        ProductImportService(fakeDeliveryRepo, fakeImportRepo, maxItems, chunkSize = 5)

    private fun validItem(url: String = "https://vkusvill.ru/goods/1") = ParserImportItemDto(
        name = "Молоко 1л",
        url = url,
        price = 150,
        currency = "RUB",
        variants = emptyList()
    )

    private fun validRequest(items: List<ParserImportItemDto> = listOf(validItem())) =
        ParserImportRequest(deliveryServiceCode = "VKUSVILL", items = items)

    // ── Request-level validation ─────────────────────────────────────────────

    @Test
    fun `oversized request throws ValidationException with correct message`() {
        val items = (1..6).map { validItem("https://vkusvill.ru/goods/$it") }
        val ex = assertFailsWith<ValidationException> {
            runBlocking { service(maxItems = 5).importBatch(validRequest(items)) }
        }
        assert(ex.message.contains("6")) { "message should contain actual count: ${ex.message}" }
        assert(ex.message.contains("5")) { "message should contain max: ${ex.message}" }
    }

    @Test
    fun `request at exactly maxItemsPerRequest is accepted`() = runBlocking {
        val items = (1..5).map { validItem("https://vkusvill.ru/goods/$it") }
        val result = service(maxItems = 5).importBatch(validRequest(items))
        assertEquals(5, result.importedCount)
        assertEquals(0, result.failedCount)
    }

    @Test
    fun `empty items list throws ValidationException`() {
        assertFailsWith<ValidationException> {
            runBlocking { service().importBatch(validRequest(emptyList())) }
        }
    }

    // ── deliveryServiceCode validation ───────────────────────────────────────

    @Test
    fun `blank deliveryServiceCode throws ValidationException`() {
        assertFailsWith<ValidationException> {
            runBlocking { service().importBatch(ParserImportRequest("   ", listOf(validItem()))) }
        }
    }

    @Test
    fun `unknown deliveryServiceCode returns error result`() = runBlocking {
        val result = service().importBatch(ParserImportRequest("UNKNOWN", listOf(validItem())))
        assertEquals(0, result.importedCount)
        assert(result.errors.first().message.contains("UNKNOWN"))
    }

    // ── Item-level validation ─────────────────────────────────────────────────

    @Test
    fun `blank name fails item`() = runBlocking {
        val result = service().importBatch(validRequest(listOf(validItem().copy(name = "  "))))
        assertEquals(0, result.importedCount)
        assertEquals(1, result.failedCount)
        assert(result.errors.first().message.contains("name"))
    }

    @Test
    fun `blank url fails item`() = runBlocking {
        val result = service().importBatch(validRequest(listOf(validItem().copy(url = ""))))
        assertEquals(0, result.importedCount)
        assertEquals(1, result.failedCount)
    }

    @Test
    fun `relative url fails item`() = runBlocking {
        val result = service().importBatch(validRequest(listOf(validItem().copy(url = "/goods/1"))))
        assertEquals(0, result.importedCount)
        assert(result.errors.first().message.contains("абсолютным"))
    }

    @Test
    fun `non http url fails item`() = runBlocking {
        val result = service().importBatch(validRequest(listOf(validItem().copy(url = "ftp://example.com/file"))))
        assertEquals(0, result.importedCount)
        assert(result.errors.first().message.contains("абсолютным"))
    }

    @Test
    fun `negative price fails item`() = runBlocking {
        val result = service().importBatch(validRequest(listOf(validItem().copy(price = -1))))
        assertEquals(0, result.importedCount)
        assert(result.errors.first().message.contains("price"))
    }

    @Test
    fun `blank currency fails item`() = runBlocking {
        val result = service().importBatch(validRequest(listOf(validItem().copy(currency = "  "))))
        assertEquals(0, result.importedCount)
        assert(result.errors.first().message.contains("currency"))
    }

    @Test
    fun `zero weight fails item`() = runBlocking {
        val variant = ParserImportVariantDto(weight = 0)
        val result = service().importBatch(validRequest(listOf(validItem().copy(variants = listOf(variant)))))
        assertEquals(0, result.importedCount)
        assert(result.errors.first().message.contains("weight"))
    }

    @Test
    fun `negative weight fails item`() = runBlocking {
        val variant = ParserImportVariantDto(weight = -5)
        val result = service().importBatch(validRequest(listOf(validItem().copy(variants = listOf(variant)))))
        assertEquals(0, result.importedCount)
    }

    @Test
    fun `negative calories fails item`() = runBlocking {
        val variant = ParserImportVariantDto(nutrients = ParserImportNutrientsDto(calories = -1))
        val result = service().importBatch(validRequest(listOf(validItem().copy(variants = listOf(variant)))))
        assertEquals(0, result.importedCount)
        assert(result.errors.first().message.contains("calories"))
    }

    @Test
    fun `negative protein fails item`() = runBlocking {
        val variant = ParserImportVariantDto(nutrients = ParserImportNutrientsDto(protein = -0.1))
        val result = service().importBatch(validRequest(listOf(validItem().copy(variants = listOf(variant)))))
        assertEquals(0, result.importedCount)
    }

    @Test
    fun `negative fat fails item`() = runBlocking {
        val variant = ParserImportVariantDto(nutrients = ParserImportNutrientsDto(fat = -1.0))
        val result = service().importBatch(validRequest(listOf(validItem().copy(variants = listOf(variant)))))
        assertEquals(0, result.importedCount)
    }

    @Test
    fun `negative carbs fails item`() = runBlocking {
        val variant = ParserImportVariantDto(nutrients = ParserImportNutrientsDto(carbs = -0.5))
        val result = service().importBatch(validRequest(listOf(validItem().copy(variants = listOf(variant)))))
        assertEquals(0, result.importedCount)
    }

    // ── Valid item passes ─────────────────────────────────────────────────────

    @Test
    fun `valid item with positive weight and nutrients is imported`() = runBlocking {
        val variant = ParserImportVariantDto(
            weight = 200,
            nutrients = ParserImportNutrientsDto(calories = 120, protein = 3.5, fat = 2.0, carbs = 15.0)
        )
        val result = service().importBatch(validRequest(listOf(validItem().copy(variants = listOf(variant)))))
        assertEquals(1, result.importedCount)
        assertEquals(0, result.failedCount)
    }

    @Test
    fun `null nutrient values are accepted`() = runBlocking {
        val variant = ParserImportVariantDto(
            weight = 100,
            nutrients = ParserImportNutrientsDto(calories = null, protein = null, fat = null, carbs = null)
        )
        val result = service().importBatch(validRequest(listOf(validItem().copy(variants = listOf(variant)))))
        assertEquals(1, result.importedCount)
    }

    @Test
    fun `zero price is accepted`() = runBlocking {
        val result = service().importBatch(validRequest(listOf(validItem().copy(price = 0))))
        assertEquals(1, result.importedCount)
    }

    // ── Partial failure ───────────────────────────────────────────────────────

    @Test
    fun `valid and invalid items in same batch processes valid items`() = runBlocking {
        val items = listOf(
            validItem("https://vkusvill.ru/goods/1"),
            validItem("https://vkusvill.ru/goods/2").copy(price = -10), // invalid
            validItem("https://vkusvill.ru/goods/3")
        )
        val result = service().importBatch(validRequest(items))
        assertEquals(2, result.importedCount)
        assertEquals(1, result.failedCount)
        assertEquals(1, result.errors.size)
        assertEquals(1, result.errors.first().itemIndex) // 0-based, second item
    }
}
