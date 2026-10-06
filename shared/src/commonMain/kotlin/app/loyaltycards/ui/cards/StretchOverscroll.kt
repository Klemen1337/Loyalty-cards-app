package app.loyaltycards.ui.cards

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.sign

/**
 * Rubber-band overscroll for the card stack: dragging past the top or bottom of the list
 * builds up [stretchPx] (positive when pulling down, negative when pulling up) with growing
 * resistance, and releasing springs it back to zero. The stack uses it to spread the cards.
 */
@Stable
internal class StretchOverscrollState(private val maxStretchPx: Float) {
    var stretchPx by mutableFloatStateOf(0f)
        private set

    val connection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // While stretched, a drag back towards rest first undoes the stretch.
            val current = stretchPx
            if (current == 0f || source != NestedScrollSource.UserInput) return Offset.Zero
            if (sign(available.y) == sign(current)) return Offset.Zero
            val consumed = if (abs(available.y) > abs(current)) -current else available.y
            stretchPx = current + consumed
            return Offset(0f, consumed)
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (source != NestedScrollSource.UserInput || available.y == 0f) return Offset.Zero
            // Resistance grows as the stretch approaches its maximum.
            val resistance = 1f - (abs(stretchPx) / maxStretchPx).coerceIn(0f, 1f)
            stretchPx = (stretchPx + available.y * 0.5f * resistance).coerceIn(-maxStretchPx, maxStretchPx)
            return Offset(0f, available.y)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (stretchPx == 0f) return Velocity.Zero
            animate(
                initialValue = stretchPx,
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
            ) { value, _ -> stretchPx = value }
            return available
        }
    }
}

@Composable
internal fun rememberStretchOverscrollState(): StretchOverscrollState {
    val maxStretchPx = with(LocalDensity.current) { 240.dp.toPx() }
    return remember(maxStretchPx) { StretchOverscrollState(maxStretchPx) }
}
