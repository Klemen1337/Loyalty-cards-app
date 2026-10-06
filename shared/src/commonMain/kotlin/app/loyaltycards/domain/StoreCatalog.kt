package app.loyaltycards.domain

data class Store(
    val id: String,
    /** Brand name, e.g. "Lidl". */
    val name: String,
    /** Name shown on the card, e.g. "Lidl Plus". */
    val cardName: String,
    val category: String,
    /** Brand color as ARGB, used as the card background. */
    val colorArgb: Long,
    val secondaryColorArgb: Long?,
) {
    /** Short text shown in the card's corner when there's no logo. */
    val mark: String get() = name.first().uppercase()
}

/**
 * Stores the app knows, used for suggestions, card colors and logos. The list is generated from
 * docs/brands/brands.json by scripts/generate_brands.py.
 */
object StoreCatalog {
    val stores: List<Store> = catalogStores

    /** Colors for stores that aren't in the catalog. */
    private val fallbackColors: List<Long> = listOf(
        0xFF2D3142, 0xFF5B3E96, 0xFF0F766E, 0xFFB45309, 0xFF9D174D, 0xFF1E40AF,
    )

    fun byId(id: String?): Store? = id?.let { key -> stores.firstOrNull { it.id == key } }

    fun findByName(name: String): Store? {
        val trimmed = name.trim()
        return stores.firstOrNull {
            it.cardName.equals(trimmed, ignoreCase = true) || it.name.equals(trimmed, ignoreCase = true)
        }
    }

    /** Stores whose brand or card name contains [query], best matches first. */
    fun search(query: String): List<Store> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        return stores
            .filter { it.name.contains(q, ignoreCase = true) || it.cardName.contains(q, ignoreCase = true) }
            .sortedBy {
                if (it.name.startsWith(q, ignoreCase = true) || it.cardName.startsWith(q, ignoreCase = true)) 0 else 1
            }
    }

    /** A stable color for a store name the catalog doesn't know. */
    fun fallbackColor(name: String): Long =
        fallbackColors[(name.trim().lowercase().hashCode() and Int.MAX_VALUE) % fallbackColors.size]
}
