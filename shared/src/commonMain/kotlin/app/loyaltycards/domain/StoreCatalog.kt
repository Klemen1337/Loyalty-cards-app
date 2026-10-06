package app.loyaltycards.domain

data class Store(
    val id: String,
    val name: String,
    /** Brand color as ARGB. */
    val colorArgb: Long,
    /** Short brand mark shown on the card, e.g. "IKEA" or "M". */
    val mark: String,
)

/** Stores the app knows, used for suggestions and card colors. */
object StoreCatalog {
    val stores: List<Store> = listOf(
        Store("mercator", "Mercator Pika", 0xFFC8102E, "M"),
        Store("spar", "SPAR plus", 0xFF0B6E3A, "S"),
        Store("dm", "dm", 0xFF213A70, "dm"),
        Store("ikea", "IKEA Family", 0xFF0058A3, "IKEA"),
        Store("lidl", "Lidl Plus", 0xFF0050AA, "L"),
        Store("hofer", "Hofer", 0xFF1C2A55, "H"),
        Store("tus", "Tuš klub", 0xFFB5161B, "T"),
        Store("petrol", "Petrol Klub", 0xFF00594C, "P"),
        Store("muller", "Müller", 0xFFE5501E, "M"),
        Store("decathlon", "DECATHLON", 0xFF0082C3, "D"),
        Store("merkur", "Merkur", 0xFF1D428A, "M"),
    )

    /** Colors for stores that aren't in the catalog. */
    private val fallbackColors: List<Long> = listOf(
        0xFF2D3142, 0xFF5B3E96, 0xFF0F766E, 0xFFB45309, 0xFF9D174D, 0xFF1E40AF,
    )

    fun byId(id: String?): Store? = id?.let { key -> stores.firstOrNull { it.id == key } }

    fun findByName(name: String): Store? =
        stores.firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }

    /** Stores whose name contains [query], best matches first. */
    fun search(query: String): List<Store> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        return stores
            .filter { it.name.contains(q, ignoreCase = true) }
            .sortedBy { if (it.name.startsWith(q, ignoreCase = true)) 0 else 1 }
    }

    /** A stable color for a store name the catalog doesn't know. */
    fun fallbackColor(name: String): Long =
        fallbackColors[(name.trim().lowercase().hashCode() and Int.MAX_VALUE) % fallbackColors.size]
}
