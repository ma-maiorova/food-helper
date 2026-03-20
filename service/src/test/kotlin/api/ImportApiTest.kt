package api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.example.api.errors.installApiStatusPages
import org.example.api.routes.adminImportRoutes
import org.example.domain.DeliveryService
import org.example.repository.DeliveryServiceRepository
import org.example.repository.ProductImportRepository
import org.example.service.ProductImportService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * HTTP-level tests for POST /api/v1/admin/import.
 * Uses Ktor testApplication with a fake in-memory setup (no real DB).
 */
class ImportApiTest {

    private val knownService = DeliveryService(1L, "VKUSVILL", "ВкусВилл", null, null, true)

    private fun fakeDeliveryRepo() = object : DeliveryServiceRepository {
        override suspend fun getAll() = listOf(knownService)
        override suspend fun findById(id: Long) = if (id == 1L) knownService else null
        override suspend fun findByCode(code: String) = if (code == "VKUSVILL") knownService else null
        override suspend fun create(code: String, name: String, siteUrl: String?, logoUrl: String?, active: Boolean) =
            DeliveryService(2L, code, name, siteUrl, logoUrl, active)
        override suspend fun update(id: Long, name: String?, siteUrl: String?, logoUrl: String?, active: Boolean?) = null
        override suspend fun delete(id: Long) = false
    }

    private fun fakeImportRepo() = object : ProductImportRepository {
        override suspend fun upsertProduct(
            deliveryServiceId: Long, name: String, url: String, price: Int, currency: String
        ) = 1L
        override suspend fun replaceVariants(productId: Long, variants: List<org.example.api.dto.ParserImportVariantDto>) {}
    }

    private fun testApp(maxItems: Int = 3, block: suspend ApplicationTestBuilder.() -> Unit) =
        testApplication {
            // Prevent loading application.conf (which references the real DB module).
            environment { config = MapApplicationConfig() }
            application {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
                }
                installApiStatusPages()
                routing {
                    route("/api/v1") {
                        adminImportRoutes(
                            ProductImportService(fakeDeliveryRepo(), fakeImportRepo(), maxItems, chunkSize = 10)
                        )
                    }
                }
            }
            block()
        }

    private fun itemsJson(count: Int): String {
        val items = (1..count).joinToString(",") { i ->
            """{"name":"Product $i","url":"https://vkusvill.ru/goods/$i","price":100,"currency":"RUB","variants":[]}"""
        }
        return """{"deliveryServiceCode":"VKUSVILL","items":[$items]}"""
    }

    // ── Oversized request → 400 ───────────────────────────────────────────────

    @Test
    fun `POST import with too many items returns 400`() = testApp(maxItems = 3) {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            setBody(itemsJson(4))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("VALIDATION_ERROR", body["code"]?.jsonPrimitive?.content)
        assertTrue(body["message"]?.jsonPrimitive?.content?.contains("4") == true)
        assertTrue(body["message"]?.jsonPrimitive?.content?.contains("3") == true)
    }

    @Test
    fun `POST import at exactly maxItems returns 200`() = testApp(maxItems = 3) {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            setBody(itemsJson(3))
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    // ── Empty items → 400 ────────────────────────────────────────────────────

    @Test
    fun `POST import with empty items list returns 400`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            setBody("""{"deliveryServiceCode":"VKUSVILL","items":[]}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("VALIDATION_ERROR", body["code"]?.jsonPrimitive?.content)
    }

    // ── Valid request → 200 ──────────────────────────────────────────────────

    @Test
    fun `POST import with valid single item returns 200 with importedCount 1`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            setBody(itemsJson(1))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(1, body["importedCount"]?.jsonPrimitive?.content?.toInt())
        assertEquals(0, body["failedCount"]?.jsonPrimitive?.content?.toInt())
    }

    // ── Item-level error in response body ─────────────────────────────────────

    @Test
    fun `POST import with invalid item returns 200 with failedCount 1`() = testApp {
        val body = """
            {
              "deliveryServiceCode": "VKUSVILL",
              "items": [
                {"name":"Good","url":"https://vkusvill.ru/goods/1","price":100,"currency":"RUB","variants":[]},
                {"name":"Bad","url":"not-a-url","price":100,"currency":"RUB","variants":[]}
              ]
            }
        """.trimIndent()
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(1, json["importedCount"]?.jsonPrimitive?.content?.toInt())
        assertEquals(1, json["failedCount"]?.jsonPrimitive?.content?.toInt())
    }

    // ── Blank deliveryServiceCode → 400 ──────────────────────────────────────

    @Test
    fun `POST import with blank deliveryServiceCode returns 400`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            setBody("""{"deliveryServiceCode":"   ","items":[{"name":"Молоко","url":"https://vkusvill.ru/goods/1","price":100,"currency":"RUB","variants":[]}]}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("VALIDATION_ERROR", body["code"]?.jsonPrimitive?.content)
    }

    // ── Malformed JSON → 400 ─────────────────────────────────────────────────

    @Test
    fun `POST import with malformed JSON returns 400`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            setBody("{not valid json}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
