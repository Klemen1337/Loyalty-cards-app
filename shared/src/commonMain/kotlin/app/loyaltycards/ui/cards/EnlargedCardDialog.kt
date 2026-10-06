package app.loyaltycards.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.loyaltycards.domain.LoyaltyCard
import app.loyaltycards.ui.components.BarcodePanel
import app.loyaltycards.ui.components.CardDetails
import app.loyaltycards.ui.components.CardHeader
import app.loyaltycards.ui.components.cardContentColor
import app.loyaltycards.ui.theme.EnlargedCardBackdrop

/**
 * Full-screen card with a large barcode, for scanning at the till. On a portrait screen the
 * card is turned sideways so the barcode can use the screen's full height.
 */
@Composable
internal fun EnlargedCardDialog(card: LoyaltyCard, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = fullScreenDialogProperties(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EnlargedCardBackdrop)
                .clickable(interactionSource = null, indication = null, onClick = onDismiss)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            BoxWithConstraints(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                val portrait = maxHeight > maxWidth
                val cardModifier = if (portrait) {
                    // Lay the card out at landscape size, then turn it a quarter clockwise.
                    Modifier.requiredSize(width = maxHeight, height = maxWidth).rotate(90f)
                } else {
                    Modifier.fillMaxSize()
                }
                LandscapeCard(card, onDismiss, cardModifier)
            }
        }
    }
}

@Composable
private fun LandscapeCard(card: LoyaltyCard, onDismiss: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = Color(card.colorArgb),
        contentColor = cardContentColor(card.colorArgb),
    ) {
        Column(Modifier.padding(24.dp)) {
            CardHeader(card) {
                Spacer(Modifier.width(14.dp))
                ClosePill(onDismiss)
            }
            Spacer(Modifier.height(12.dp))
            CardDetails(card)
            Spacer(Modifier.height(16.dp))
            BarcodePanel(card = card, caption = null, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ClosePill(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        contentColor = Color(0xFF16171A),
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 18.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
            Text("Close", style = MaterialTheme.typography.labelLarge)
        }
    }
}
