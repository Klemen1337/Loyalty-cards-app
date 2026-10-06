package app.loyaltycards.domain

data class LoyaltyCard(
    val id: Long,
    /** Key into [StoreCatalog], or null for a store the app doesn't know. */
    val storeId: String?,
    val name: String,
    val cardNumber: String,
    val barcodeFormat: BarcodeFormat,
    /** Card color as ARGB, e.g. 0xFF0058A3. */
    val colorArgb: Long,
    val nameOnCard: String?,
    /** Sort order in the wallet, ascending. */
    val position: Long,
    val createdAtMillis: Long,
)
