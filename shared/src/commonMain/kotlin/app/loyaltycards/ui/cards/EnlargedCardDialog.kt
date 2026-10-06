package app.loyaltycards.ui.cards

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.loyaltycards.domain.BarcodeFormat
import app.loyaltycards.domain.LoyaltyCard
import app.loyaltycards.ui.components.BarcodePanel
import app.loyaltycards.ui.components.CardDetails
import app.loyaltycards.ui.components.CardHeader
import app.loyaltycards.ui.components.cardContentColor
import app.loyaltycards.ui.theme.EnlargedCardBackdrop

/** Full-screen card with a large barcode, for scanning at the till. */
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
                .clickable(interactionSource = null, indication = null, onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color(card.colorArgb),
                contentColor = cardContentColor(card.colorArgb),
            ) {
                Column(Modifier.padding(24.dp)) {
                    CardHeader(card) {
                        Spacer(Modifier.width(14.dp))
                        ClosePill(onDismiss)
                    }
                    Spacer(Modifier.height(20.dp))
                    CardDetails(card)
                    Spacer(Modifier.height(20.dp))
                    val is2d = card.barcodeFormat in setOf(
                        BarcodeFormat.QR_CODE,
                        BarcodeFormat.AZTEC,
                        BarcodeFormat.DATA_MATRIX,
                    )
                    BarcodePanel(
                        card = card,
                        caption = null,
                        modifier = Modifier.height(if (is2d) 300.dp else 220.dp),
                    )
                }
            }
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
