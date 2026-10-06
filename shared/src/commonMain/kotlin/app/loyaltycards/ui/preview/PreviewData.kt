package app.loyaltycards.ui.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import app.loyaltycards.domain.BarcodeFormat
import app.loyaltycards.domain.LoyaltyCard
import app.loyaltycards.ui.theme.LoyaltyCardsTheme

/** Sample cards for @Preview functions. */
internal object PreviewData {
    val cards: List<LoyaltyCard> = listOf(
        card(1, "mercator", "Mercator Pika", "2400118352905", BarcodeFormat.EAN_13, 0xFFDF134C, "Klemen Kast"),
        card(2, "spar", "SPAR plus", "9120045716283", BarcodeFormat.EAN_13, 0xFFDE0405, "Klemen Kast"),
        card(3, "dm", "dm", "4066447208818", BarcodeFormat.EAN_13, 0xFF002878),
        card(4, "ikea", "IKEA Family", "6275980017342210", BarcodeFormat.CODE_128, 0xFF0058A3),
        card(5, "lidl", "Lidl Plus", "3820117640398", BarcodeFormat.EAN_13, 0xFF0050AA),
        card(6, "hofer", "Hofer", "2900008181641", BarcodeFormat.EAN_13, 0xFF00005F),
        card(7, null, "Gym membership", "https://example.com/member/48213", BarcodeFormat.QR_CODE, 0xFF0F766E),
    )

    val ikea: LoyaltyCard get() = cards.first { it.storeId == "ikea" }
    val qrCard: LoyaltyCard get() = cards.first { it.barcodeFormat == BarcodeFormat.QR_CODE }

    private fun card(
        id: Long,
        storeId: String?,
        name: String,
        code: String,
        format: BarcodeFormat,
        color: Long,
        nameOnCard: String? = null,
    ) = LoyaltyCard(
        id = id,
        storeId = storeId,
        name = name,
        cardNumber = code,
        barcodeFormat = format,
        colorArgb = color,
        nameOnCard = nameOnCard,
        position = id,
        createdAtMillis = 0,
    )
}

/** App theme on the page background, for previews. */
@Composable
internal fun PreviewTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    LoyaltyCardsTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background, content = content)
    }
}
