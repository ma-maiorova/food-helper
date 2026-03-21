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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.example.api.auth.installApiKeyAuth
import org.example.api.errors.installApiStatusPages
import org.example.api.routes.adminImportRoutes
import org.example.domain.DeliveryService
import org.example.repository.DeliveryServiceRepository
import org.example.repository.ProductImportRepository
import org.example.repository.UpsertResult
import org.example.service.ProductImportService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * HTTP-level tests for POST /api/v1/admin/import.
 * Uses Ktor testApplication with a fake in-memory setup (no real DB).
 */
class ImportApiTest {

    private val testApiKey = "test-secret-key"
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

    private var nextProductId = 1L

    private fun fakeImportRepo() = object : ProductImportRepository {
        override suspend fun upsertProduct(
            deliveryServiceId: Long, name: String, url: String, price: Int, currency: String
        ) = UpsertResult(productId = nextProductId++, created = true)
        override suspend fun replaceVariants(productId: Long, variants: List<org.example.api.dto.ParserImportVariantDto>) {}
    }

    private fun testApp(maxItems: Int = 10, block: suspend ApplicationTestBuilder.() -> Unit) =
        testApplication {
            environment { config = MapApplicationConfig() }
            application {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
                }
                installApiStatusPages()
                routing {
                    route("/api/v1") {
                        route("/admin") {
                            installApiKeyAuth(testApiKey)
                            adminImportRoutes(
                                ProductImportService(fakeDeliveryRepo(), fakeImportRepo(), maxItems, chunkSize = 10)
                            )
                        }
                    }
                }
            }
            block()
        }

    /** Valid item with one variant (non-empty variants required). */
    private fun singleItemJson(name: String = "Product", url: String, price: Int = 100) =
        """{"name":"$name","url":"$url","price":$price,"currency":"RUB","variants":[{"nutrients":null}]}"""

    private fun itemsJson(count: Int): String {
        val items = (1..count).joinToString(",") { i ->
            singleItemJson(url = "https://vkusvill.ru/goods/$i")
        }
        return """{"deliveryServiceCode":"VKUSVILL","items":[$items]}"""
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    @Test
    fun `POST import without X-Api-Key returns 401`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            setBody(itemsJson(1))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("UNAUTHORIZED", body["code"]?.jsonPrimitive?.content)
    }

    @Test
    fun `POST import with wrong X-Api-Key returns 401`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", "wrong-key")
            setBody(itemsJson(1))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST import with correct X-Api-Key succeeds`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", testApiKey)
            setBody(itemsJson(1))
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    // ── Oversized request → 400 ───────────────────────────────────────────────

    @Test
    fun `POST import with too many items returns 400`() = testApp(maxItems = 3) {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", testApiKey)
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
            header("X-Api-Key", testApiKey)
            setBody(itemsJson(3))
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    // ── Empty items → 400 ────────────────────────────────────────────────────

    @Test
    fun `POST import with empty items list returns 400`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", testApiKey)
            setBody("""{"deliveryServiceCode":"VKUSVILL","items":[]}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("VALIDATION_ERROR", body["code"]?.jsonPrimitive?.content)
    }

    // ── Valid request → 200 ──────────────────────────────────────────────────

    @Test
    fun `POST import with valid single item returns 200 with created 1`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", testApiKey)
            setBody(itemsJson(1))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(1, body["created"]?.jsonPrimitive?.content?.toInt())
        assertEquals(0, body["failed"]?.jsonPrimitive?.content?.toInt())
        assertEquals(1, body["totalReceived"]?.jsonPrimitive?.content?.toInt())
        assertEquals(0, body["duplicatesResolved"]?.jsonPrimitive?.content?.toInt())
        assertNotNull(body["durationMs"])
    }

    // ── Item-level: invalid URL → error with error code ───────────────────────

    @Test
    fun `POST import with invalid URL item returns 200 with failed 1 and error code`() = testApp {
        val body = """
            {
              "deliveryServiceCode": "VKUSVILL",
              "items": [
                ${singleItemJson(url = "https://vkusvill.ru/goods/1")},
                {"name":"Bad","url":"not-a-url","price":100,"currency":"RUB","variants":[{"nutrients":null}]}
              ]
            }
        """.trimIndent()
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", testApiKey)
            setBody(body)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(1, json["created"]?.jsonPrimitive?.content?.toInt())
        assertEquals(1, json["failed"]?.jsonPrimitive?.content?.toInt())
        val errors = json["errors"]?.jsonArray
        assertEquals("invalid_url", errors?.first()?.jsonObject?.get("errorCode")?.jsonPrimitive?.content)
    }

    // ── Item-level: empty variants → empty_variants error code ───────────────

    @Test
    fun `POST import item with empty variants returns failed with empty_variants code`() = testApp {
        val body = """
            {
              "deliveryServiceCode": "VKUSVILL",
              "items": [
                {"name":"NoVariants","url":"https://vkusvill.ru/goods/1","price":100,"currency":"RUB","variants":[]}
              ]
            }
        """.trimIndent()
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", testApiKey)
            setBody(body)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(0, json["created"]?.jsonPrimitive?.content?.toInt())
        assertEquals(1, json["failed"]?.jsonPrimitive?.content?.toInt())
        val errors = json["errors"]?.jsonArray
        assertEquals("empty_variants", errors?.first()?.jsonObject?.get("errorCode")?.jsonPrimitive?.content)
    }

    // ── Duplicate URL in batch → last wins ───────────────────────────────────

    @Test
    fun `POST import duplicate URL in batch uses last wins and sets duplicatesResolved`() = testApp {
        val body = """
            {
              "deliveryServiceCode": "VKUSVILL",
              "items": [
                ${singleItemJson(name = "First", url = "https://vkusvill.ru/goods/1", price = 100)},
                ${singleItemJson(name = "Second", url = "https://vkusvill.ru/goods/1", price = 200)}
              ]
            }
        """.trimIndent()
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", testApiKey)
            setBody(body)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(2, json["totalReceived"]?.jsonPrimitive?.content?.toInt())
        assertEquals(1, json["duplicatesResolved"]?.jsonPrimitive?.content?.toInt())
        assertEquals(1, json["created"]?.jsonPrimitive?.content?.toInt())
        assertEquals(0, json["failed"]?.jsonPrimitive?.content?.toInt())
    }

    // ── Blank deliveryServiceCode → 400 ──────────────────────────────────────

    @Test
    fun `POST import with blank deliveryServiceCode returns 400`() = testApp {
        val response = client.post("/api/v1/admin/import") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", testApiKey)
            setBody("""{"deliveryServiceCode":"   ","items":[${singleItemJson(url = "https://vkusvill.ru/goods/1")}]}""")
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
            header("X-Api-Key", testApiKey)
            setBody("{not valid json}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
