package app.loyaltycards.ui.cards

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.loyaltycards.resources.Res
import app.loyaltycards.resources.add_first_card
import app.loyaltycards.resources.empty_body
import app.loyaltycards.resources.empty_title
import app.loyaltycards.ui.components.PillButton
import app.loyaltycards.ui.preview.PreviewTheme
import org.jetbrains.compose.resources.stringResource

/** A playful fan of floating cards with a big button to add the first real one. */
@Composable
internal fun EmptyWallet(onAddCard: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CardFan()
        Spacer(Modifier.height(36.dp))
        Text(
            text = stringResource(Res.string.empty_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.empty_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        PillButton(
            text = stringResource(Res.string.add_first_card),
            onClick = onAddCard,
            modifier = Modifier.height(56.dp),
            contentPadding = PaddingValues(horizontal = 32.dp),
        )
        // Keeps the group slightly above center, where the eye lands.
        Spacer(Modifier.height(48.dp))
    }
}

private data class FanCard(val color: Color, val angle: Float, val x: Dp, val y: Dp)

private val fanCards = listOf(
    FanCard(Color(0xFF24702F), angle = -16f, x = (-46).dp, y = 14.dp),
    FanCard(Color(0xFFFFDD00), angle = 12f, x = 44.dp, y = 6.dp),
    FanCard(Color(0xFF0050AA), angle = -4f, x = (-6).dp, y = (-10).dp),
    FanCard(Color(0xFFDF134C), angle = 7f, x = 10.dp, y = 22.dp),
)

@Composable
private fun CardFan() {
    val transition = rememberInfiniteTransition()
    val bob by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    )
    Box(Modifier.size(width = 260.dp, height = 170.dp), contentAlignment = Alignment.Center) {
        fanCards.forEachIndexed { index, card ->
            // Neighbouring cards sway in opposite directions so the fan breathes.
            val sway = if (index % 2 == 0) bob else -bob
            Surface(
                modifier = Modifier
                    .size(width = 150.dp, height = 96.dp)
                    .graphicsLayer {
                        translationX = card.x.toPx()
                        translationY = card.y.toPx() + sway * 4.dp.toPx()
                        rotationZ = card.angle + sway * 2.5f
                    },
                shape = RoundedCornerShape(16.dp),
                color = card.color,
                shadowElevation = 8.dp,
            ) {
                if (index == fanCards.lastIndex) MiniCardFace()
            }
        }
    }
}

/** Top card of the fan: a name stripe and a tiny barcode. */
@Composable
private fun MiniCardFace() {
    Column(Modifier.padding(14.dp)) {
        Box(Modifier.size(width = 56.dp, height = 10.dp).background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(5.dp)))
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.fillMaxWidth().height(30.dp).background(Color.White, RoundedCornerShape(6.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(2, 1, 3, 1, 2, 1, 1, 3, 2, 1, 2, 1, 3, 1, 2).forEach { width ->
                    Box(Modifier.width(width.dp).fillMaxHeight().background(Color(0xFF16171A)))
                }
            }
        }
    }
}

@Preview(name = "Empty wallet", widthDp = 390, heightDp = 700)
@Composable
private fun EmptyWalletPreview() = PreviewTheme {
    EmptyWallet(onAddCard = {})
}
