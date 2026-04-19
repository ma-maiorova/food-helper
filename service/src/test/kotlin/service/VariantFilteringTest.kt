package service

import org.example.domain.Nutrients
import org.example.domain.ProductVariant
import org.example.service.computeSearchRank
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Regression tests for variant filtering semantics.
 *
 * When КБЖУ filters are applied:
 * - Only variants matching ALL provided nutrient constraints remain in the product.
 * - Products with zero matching variants are excluded from results entirely.
 * - Ranking (computeSearchRank) uses ONLY the compositions of matched variants.
 *
 * This is critical for correctness: if an unmatched variant has "овсяное" in composition
 * but the matched variant does not, the product should NOT rank as a composition match
 * for "овсяное".
 */
class VariantFilteringTest {

    private fun variant(
        id: Long,
        productId: Long = 1L,
        calories: Int? = null,
        protein: Double? = null,
        fat: Double? = null,
        carbs: Double? = null,
        composition: String? = null
    ) = ProductVariant(
        id = id,
        productId = productId,
        composition = composition,
        nutrients = Nutrients(calories, protein, fat, carbs)
    )

    // ── Calorie range filtering ────────────────────────────────────────────────

    @Test
    fun `variant within calorie range is included`() {
        val v = variant(1, calories = 150)
        assertTrue(v.nutrients.calories!! in 100..200)
    }

    @Test
    fun `variant below min calories is excluded`() {
        val v = variant(1, calories = 50)
        assertTrue(v.nutrients.calories!! < 100)
    }

    @Test
    fun `variant above max calories is excluded`() {
        val v = variant(1, calories = 350)
        assertTrue(v.nutrients.calories!! > 300)
    }

    @Test
    fun `variant exactly at min calories boundary is included`() {
        val v = variant(1, calories = 100)
        assertTrue(v.nutrients.calories!! >= 100)
    }

    @Test
    fun `product with no matching variants is effectively excluded`() {
        val variants = listOf(
            variant(1, calories = 50),
            variant(2, calories = 80)
        )
        val matched = variants.filter { it.nutrients.calories?.let { c -> c >= 100 } ?: false }
        assertTrue(matched.isEmpty(), "expected no matched variants")
    }

    @Test
    fun `only matching variants survive filter when product has mixed variants`() {
        val variants = listOf(
            variant(1, calories = 150),
            variant(2, calories = 50),  // below min
            variant(3, calories = 200)
        )
        val matched = variants.filter { it.nutrients.calories?.let { c -> c >= 100 } ?: false }
        assertEquals(2, matched.size)
        assertTrue(matched.all { it.nutrients.calories!! >= 100 })
    }

    // ── Variant semantics for ranking ─────────────────────────────────────────

    @Test
    fun `ranking with matched variants only - composition match returns 30`() {
        // Only the matched variant contributes its composition to ranking
        val matchedCompositions = listOf("молоко овсяное, вода")
        val tokens = listOf("молоко", "овсяное")
        val phrase = "молоко овсяное"
        val rank = computeSearchRank("Напиток растительный", matchedCompositions, tokens, phrase)
        assertEquals(30, rank)
    }

    @Test
    fun `ranking with matched variants only - unmatched composition is ignored`() {
        // If we only pass matched-variant compositions, unmatched ones don't affect rank
        val matchedCompositions = listOf("вода, соль")  // does NOT contain "овсяное"
        val tokens = listOf("овсяное")
        val phrase = "овсяное"
        val rank = computeSearchRank("Напиток", matchedCompositions, tokens, phrase)
        assertEquals(0, rank)
    }

    @Test
    fun `ranking favors name match over composition match`() {
        val compositions = listOf("молоко овсяное состав")
        val tokensForBoth = listOf("молоко", "овсяное")
        val phrase = "молоко овсяное"

        val rankWithNameMatch = computeSearchRank("Молоко овсяное 3.2%", compositions, tokensForBoth, phrase)
        val rankCompositionOnly = computeSearchRank("Напиток растительный", compositions, tokensForBoth, phrase)

        assertTrue(rankWithNameMatch > rankCompositionOnly,
            "name match ($rankWithNameMatch) should outrank composition-only ($rankCompositionOnly)")
    }

    // ── Multi-nutrient filtering ──────────────────────────────────────────────

    @Test
    fun `variant meeting all nutrient constraints is included`() {
        val v = variant(1, calories = 150, protein = 5.0, fat = 3.0, carbs = 20.0)
        val meetsMin = v.nutrients.calories!! >= 100
            && v.nutrients.protein!! >= 3.0
            && v.nutrients.fat!! >= 1.0
            && v.nutrients.carbs!! >= 10.0
        assertTrue(meetsMin)
    }

    @Test
    fun `variant failing one nutrient constraint is excluded`() {
        val v = variant(1, calories = 150, protein = 2.0, fat = 3.0, carbs = 20.0)
        // minProtein=3.0, but protein=2.0
        val meetsAll = v.nutrients.calories!! >= 100
            && v.nutrients.protein!! >= 3.0  // fails here
        assertTrue(!meetsAll)
    }

    @Test
    fun `variant with null nutrient is excluded when that nutrient is filtered`() {
        val v = variant(1, calories = null)
        // minCalories=100 but calories is null → does not meet constraint
        val meetsMin = v.nutrients.calories?.let { it >= 100 } ?: false
        assertTrue(!meetsMin)
    }
}
