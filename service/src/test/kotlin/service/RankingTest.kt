package service

import org.example.service.computeSearchRank
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for search ranking logic.
 *
 * Ranking contract:
 *  100 — exact normalized phrase in product name
 *   80 — all tokens present in product name
 *   50 — mixed: some tokens in name, remaining tokens covered by a composition variant
 *   30 — all tokens only in composition variants
 *    0 — no match
 */
class RankingTest {

    private fun rank(name: String, compositions: List<String>, query: String): Int {
        val tokens = query.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val phrase = tokens.joinToString(" ")
        return computeSearchRank(name, compositions, tokens, phrase)
    }

    // ── 100: exact phrase ─────────────────────────────────────────────────────

    @Test
    fun `exact phrase in name returns 100`() {
        assertEquals(100, rank("Молоко овсяное 3.2%", emptyList(), "молоко овсяное"))
    }

    @Test
    fun `exact phrase match is case insensitive`() {
        assertEquals(100, rank("МОЛОКО ОВСЯНОЕ", emptyList(), "молоко овсяное"))
    }

    @Test
    fun `single token exact match in name returns 100`() {
        // single token: phrase == token, so "contains phrase" fires before "all tokens in name"
        assertEquals(100, rank("Молоко 1л", emptyList(), "молоко"))
    }

    // ── 80: all tokens in name ────────────────────────────────────────────────

    @Test
    fun `all tokens in name but not as phrase returns 80`() {
        // "овсяное молоко" — tokens both present but order differs from query "молоко овсяное"
        assertEquals(80, rank("Овсяное молоко Extra", emptyList(), "молоко овсяное"))
    }

    @Test
    fun `all tokens in name case insensitive returns 80`() {
        assertEquals(80, rank("ОВСЯНОЕ МОЛОКО EXTRA", emptyList(), "молоко овсяное"))
    }

    // ── 50: mixed name + composition ─────────────────────────────────────────

    @Test
    fun `some tokens in name rest in composition returns 50`() {
        // token "овсяное" must appear verbatim in composition — "овсяные" is a different word
        val compositions = listOf("содержит овсяное молоко и воду")
        assertEquals(50, rank("Молоко 1л", compositions, "молоко овсяное"))
    }

    @Test
    fun `mixed match across multiple compositions returns 50`() {
        // "молоко" in name, "овсяные" somewhere in compositions
        val compositions = listOf("вода, соль", "овсяные хлопья, сахар")
        assertEquals(50, rank("Молоко растительное", compositions, "молоко овсяные"))
    }

    // ── 30: all tokens only in composition ───────────────────────────────────

    @Test
    fun `all tokens only in composition returns 30`() {
        val compositions = listOf("молоко овсяное, вода, соль")
        assertEquals(30, rank("Напиток растительный", compositions, "молоко овсяное"))
    }

    @Test
    fun `all tokens in composition case insensitive returns 30`() {
        val compositions = listOf("МОЛОКО ОВСЯНОЕ")
        assertEquals(30, rank("Напиток", compositions, "молоко овсяное"))
    }

    // ── 0: no match ───────────────────────────────────────────────────────────

    @Test
    fun `no match returns 0`() {
        assertEquals(0, rank("Кефир 2.5%", listOf("молоко, сахар"), "гречка"))
    }

    @Test
    fun `empty tokens returns 0`() {
        assertEquals(0, rank("Молоко", emptyList(), "   "))
    }

    // ── ranking order ─────────────────────────────────────────────────────────

    @Test
    fun `ranking order is 100 80 50 30`() {
        val comp = listOf("молоко овсяное состав")
        val r100 = rank("Молоко овсяное 3.2%", comp, "молоко овсяное")
        val r80 = rank("Овсяное молоко 1л", comp, "молоко овсяное")
        val r50 = rank("Молоко растительное", comp, "молоко овсяное")
        val r30 = rank("Напиток", comp, "молоко овсяное")

        assert(r100 > r80) { "100 > 80 failed: $r100 vs $r80" }
        assert(r80 > r50) { "80 > 50 failed: $r80 vs $r50" }
        assert(r50 > r30) { "50 > 30 failed: $r50 vs $r30" }
        assert(r30 > 0) { "30 > 0 failed: $r30" }
    }
}
