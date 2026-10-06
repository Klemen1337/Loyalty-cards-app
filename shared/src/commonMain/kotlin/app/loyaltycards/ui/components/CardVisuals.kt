package app.loyaltycards.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.loyaltycards.domain.BarcodeFormat
import app.loyaltycards.domain.LoyaltyCard
import app.loyaltycards.domain.formatCardCode
import app.loyaltycards.resources.Res
import app.loyaltycards.resources.barcode_description
import app.loyaltycards.resources.enlarge_barcode
import app.loyaltycards.resources.label_code
import app.loyaltycards.resources.label_name_on_card
import app.loyaltycards.resources.tap_barcode_to_enlarge
import app.loyaltycards.ui.preview.PreviewData
import app.loyaltycards.ui.preview.PreviewTheme
import io.github.alexzhirkevich.qrose.oned.BarcodePainter
import io.github.alexzhirkevich.qrose.oned.BarcodeType
import io.github.alexzhirkevich.qrose.oned.rememberBarcodePainter
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal object WalletCardDefaults {
    /** Visible strip of a card that sits under the next one in the stack. */
    val PeekHeight: Dp = 64.dp
    val FullHeight: Dp = 164.dp
    val BarcodePanelHeight: Dp = 128.dp
    val ExpandedHeight: Dp = FullHeight + BarcodePanelHeight + 20.dp
    val Shape = RoundedCornerShape(24.dp)
}

/** White or near-black text, whichever reads better on the card color. */
internal fun cardContentColor(colorArgb: Long): Color =
    if (Color(colorArgb).luminance() > 0.6f) Color(0xFF16171A) else Color.White

/**
 * A card as it appears in the wallet: store name and mark on top, the code at the bottom
 * and, when [expanded], a barcode panel below.
 */
@Composable
internal fun WalletCard(
    card: LoyaltyCard,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** Called when the barcode panel of an expanded card is tapped. */
    onBarcodeClick: () -> Unit = {},
) {
    val height by animateDpAsState(
        if (expanded) WalletCardDefaults.ExpandedHeight else WalletCardDefaults.FullHeight,
    )
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(height),
        shape = WalletCardDefaults.Shape,
        color = Color(card.colorArgb),
        contentColor = cardContentColor(card.colorArgb),
        shadowElevation = 6.dp,
    ) {
        Column(Modifier.padding(horizontal = 22.dp)) {
            Column(Modifier.height(WalletCardDefaults.FullHeight).padding(vertical = 20.dp)) {
                CardHeader(card)
                Spacer(Modifier.weight(1f))
                CardDetails(card)
            }
            BarcodePanel(
                card = card,
                caption = stringResource(Res.string.tap_barcode_to_enlarge),
                // A collapsed card still lays out its (hidden) panel, so only an expanded one reacts.
                onClick = if (expanded) onBarcodeClick else null,
                modifier = Modifier.height(WalletCardDefaults.BarcodePanelHeight),
            )
        }
    }
}

@Composable
internal fun CardHeader(card: LoyaltyCard, modifier: Modifier = Modifier, trailing: @Composable () -> Unit = {}) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = card.name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(12.dp))
        BrandMark(card, logoSize = 38.dp)
        trailing()
    }
}

/** The store's logo, tinted to the card's text color. Shows nothing when there's no logo. */
@Composable
internal fun BrandMark(card: LoyaltyCard, logoSize: Dp) {
    val logo = brandLogo(card.storeId) ?: return
    Icon(painterResource(logo), contentDescription = null, modifier = Modifier.size(logoSize))
}

/** "NAME ON CARD" on the left (when set) and "CODE" on the right. */
@Composable
internal fun CardDetails(card: LoyaltyCard, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        val nameOnCard = card.nameOnCard
        if (!nameOnCard.isNullOrBlank()) {
            LabeledValue(label = stringResource(Res.string.label_name_on_card), value = nameOnCard, alignEnd = false, modifier = Modifier.weight(1f))
        } else {
            Spacer(Modifier.weight(1f))
        }
        LabeledValue(
            label = stringResource(Res.string.label_code),
            value = formatCardCode(card.cardNumber, card.barcodeFormat),
            alignEnd = true,
        )
    }
}

@Composable
private fun LabeledValue(label: String, value: String, alignEnd: Boolean, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = LocalContentColor.current.copy(alpha = 0.75f),
            modifier = Modifier.padding(bottom = 2.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 19.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
        )
    }
}

/** White panel with the card's barcode, as shown on an expanded card. */
@Composable
internal fun BarcodePanel(
    card: LoyaltyCard,
    caption: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val enlargeLabel = stringResource(Res.string.enlarge_barcode)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable(onClickLabel = enlargeLabel, onClick = onClick) else Modifier),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        contentColor = Color(0xFF16171A),
    ) {
        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CardCode(
                code = card.cardNumber,
                format = card.barcodeFormat,
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
            if (caption != null) {
                Text(caption, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6E6C67))
            }
        }
    }
}

/** Draws [code] as the matching barcode or 2D code. */
@Composable
internal fun CardCode(code: String, format: BarcodeFormat, modifier: Modifier = Modifier) {
    val isLinear = format !in setOf(
        BarcodeFormat.QR_CODE,
        BarcodeFormat.PDF_417,
        BarcodeFormat.AZTEC,
        BarcodeFormat.DATA_MATRIX,
    )
    Box(modifier, contentAlignment = Alignment.Center) {
        val painter = rememberCodePainter(code, format)
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = stringResource(Res.string.barcode_description, formatCardCode(code, format)),
                modifier = Modifier.fillMaxSize(),
                contentScale = if (isLinear) ContentScale.FillBounds else ContentScale.Fit,
            )
        } else {
            // TODO: draw PDF417, Aztec and Data Matrix once a matrix encoder is added.
            Text(
                text = formatCardCode(code, format),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun rememberCodePainter(code: String, format: BarcodeFormat): Painter? = key(code, format) {
    when (format) {
        BarcodeFormat.QR_CODE -> rememberQrCodePainter(code)
        BarcodeFormat.PDF_417, BarcodeFormat.AZTEC, BarcodeFormat.DATA_MATRIX -> null
        else -> {
            val type = when (format) {
                BarcodeFormat.EAN_13 -> BarcodeType.EAN13
                BarcodeFormat.EAN_8 -> BarcodeType.EAN8
                BarcodeFormat.UPC_A -> BarcodeType.UPCA
                BarcodeFormat.CODE_39 -> BarcodeType.Code39
                else -> BarcodeType.Code128
            }
            rememberBarcodePainter(code, type, onError = { code128OrBlank(code) })
        }
    }
}

private fun code128OrBlank(code: String): Painter =
    runCatching { BarcodePainter(code, BarcodeType.Code128) }
        .getOrElse { ColorPainter(Color.Transparent) }

/** Compact card row used in edit mode and the remove sheet. */
@Composable
internal fun CardStrip(card: LoyaltyCard, modifier: Modifier = Modifier, showCodeEnding: Boolean = true) {
    Surface(
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color(card.colorArgb),
        contentColor = cardContentColor(card.colorArgb),
    ) {
        Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = card.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            if (showCodeEnding) {
                Text(
                    text = "•••• ${card.cardNumber.takeLast(4)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(alpha = 0.8f),
                )
            } else {
                BrandMark(card, logoSize = 24.dp)
            }
        }
    }
}

@Preview(name = "Wallet card")
@Composable
private fun WalletCardPreview() = PreviewTheme {
    WalletCard(card = PreviewData.cards.first(), expanded = false, onClick = {}, modifier = Modifier.padding(16.dp))
}

@Preview(name = "Wallet card, expanded")
@Composable
private fun WalletCardExpandedPreview() = PreviewTheme {
    WalletCard(card = PreviewData.ikea, expanded = true, onClick = {}, modifier = Modifier.padding(16.dp))
}

@Preview(name = "Wallet card, QR code")
@Composable
private fun WalletCardQrPreview() = PreviewTheme {
    WalletCard(card = PreviewData.qrCard, expanded = true, onClick = {}, modifier = Modifier.padding(16.dp))
}

@Preview(name = "Card strip")
@Composable
private fun CardStripPreview() = PreviewTheme {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PreviewData.cards.take(3).forEach { CardStrip(it) }
    }
}
