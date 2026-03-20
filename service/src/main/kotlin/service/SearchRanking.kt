package org.example.service

/**
 * Computes relevance rank for a product in search results.
 *
 * Scoring (highest wins):
 * - 100: exact normalized phrase found in product name
 * - 80:  all tokens found in product name
 * - 50:  mixed — some tokens in name, remaining tokens covered by at least one composition variant
 * - 30:  all tokens found only in composition variants (not in name at all)
 * - 0:   no deterministic match (should not appear because SQL already filters by AND-semantics)
 *
 * All comparisons are case-insensitive. [tokens] must be pre-lowercased; [fullPhrase] is their
 * space-joined form.
 */
internal fun computeSearchRank(
    productName: String,
    variantCompositions: List<String>,
    tokens: List<String>,
    fullPhrase: String
): Int {
    if (tokens.isEmpty()) return 0
    val nameLower = productName.lowercase()

    // Exact phrase in name
    if (nameLower.contains(fullPhrase)) return 100

    // All tokens in name
    if (tokens.all { nameLower.contains(it) }) return 80

    // Mixed: some tokens in name, remaining tokens all covered by at least one composition variant
    val inName = tokens.filter { nameLower.contains(it) }
    val notInName = tokens.filterNot { nameLower.contains(it) }
    if (inName.isNotEmpty() && notInName.isNotEmpty()) {
        val restCoveredByComp = variantCompositions.any { comp ->
            val c = comp.lowercase()
            notInName.all { c.contains(it) }
        }
        if (restCoveredByComp) return 50
    }

    // All tokens in composition only (no token found in name)
    if (variantCompositions.any { comp ->
            val c = comp.lowercase()
            tokens.all { c.contains(it) }
        }) return 30

    return 0
}
