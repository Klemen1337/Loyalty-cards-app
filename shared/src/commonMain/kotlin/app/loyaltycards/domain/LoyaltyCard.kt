package app.loyaltycards.domain

data class LoyaltyCard(
    val id: Long,
    val name: String,
    val cardNumber: String,
    val barcodeFormat: BarcodeFormat,
    /** Brand color as ARGB, e.g. 0xFF0058A3. */
    val colorArgb: Long,
    /** Sort order in the card list, ascending. */
    val position: Long,
    val createdAtMillis: Long,
)
