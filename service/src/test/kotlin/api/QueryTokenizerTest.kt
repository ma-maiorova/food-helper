package api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for q query tokenization semantics.
 *
 * Tokenization contract (used in both ProductRepositoryImpl and computeSearchRank):
 * - trim leading/trailing whitespace
 * - lowercase
 * - split by one or more whitespace characters
 * - filter out empty tokens
 *
 * AND semantics: every token must match either product.name or product_variant.composition.
 */
class QueryTokenizerTest {

    /** Mirrors the tokenization logic used in ProductRepositoryImpl and searchWithRanking. */
    private fun tokenize(q: String): List<String> =
        q.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }

    @Test
    fun `empty string produces no tokens`() {
        assertTrue(tokenize("").isEmpty())
    }

    @Test
    fun `blank string produces no tokens`() {
        assertTrue(tokenize("   ").isEmpty())
    }

    @Test
    fun `single word produces one token`() {
        assertEquals(listOf("молоко"), tokenize("молоко"))
    }

    @Test
    fun `two words produce two tokens`() {
        assertEquals(listOf("молоко", "овсяное"), tokenize("молоко овсяное"))
    }

    @Test
    fun `multiple spaces are collapsed`() {
        assertEquals(listOf("молоко", "овсяное"), tokenize("молоко   овсяное"))
    }

    @Test
    fun `leading and trailing whitespace is stripped`() {
        assertEquals(listOf("молоко", "овсяное"), tokenize("  молоко овсяное  "))
    }

    @Test
    fun `uppercase is lowercased`() {
        assertEquals(listOf("молоко", "овсяное"), tokenize("МОЛОКО ОВСЯНОЕ"))
    }

    @Test
    fun `mixed case is lowercased`() {
        assertEquals(listOf("молоко", "овсяное"), tokenize("Молоко Овсяное"))
    }

    @Test
    fun `phrase is tokens joined with single space`() {
        val tokens = tokenize("молоко овсяное")
        assertEquals("молоко овсяное", tokens.joinToString(" "))
    }

    @Test
    fun `tab is treated as whitespace separator`() {
        assertEquals(listOf("молоко", "овсяное"), tokenize("молоко\tовсяное"))
    }

    @Test
    fun `three tokens are all extracted`() {
        val tokens = tokenize("молоко овсяное 1л")
        assertEquals(3, tokens.size)
        assertEquals(listOf("молоко", "овсяное", "1л"), tokens)
    }
}
