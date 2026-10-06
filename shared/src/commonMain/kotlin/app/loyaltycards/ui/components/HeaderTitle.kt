package app.loyaltycards.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/** Height shared by the header title block and the header buttons next to it. */
internal val HeaderHeight: Dp = 48.dp

private val TitleSize = 28.sp

/** Cap height of DM Sans as a share of its font size. */
private const val CapHeightRatio = 0.7f

/**
 * Screen title with a small subtitle under it, exactly as tall as the header buttons:
 * the title's capital letters start at the buttons' top edge and the subtitle sits on
 * their bottom edge.
 */
@Composable
internal fun HeaderTitle(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Layout(
        modifier = modifier,
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall.copy(fontSize = TitleSize, lineHeight = TitleSize),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        },
    ) { measurables, constraints ->
        val textConstraints = Constraints(maxWidth = constraints.maxWidth)
        val titlePlaceable = measurables[0].measure(textConstraints)
        val subtitlePlaceable = measurables[1].measure(textConstraints)
        val height = HeaderHeight.roundToPx()
        val width = constraints.constrainWidth(maxOf(titlePlaceable.width, subtitlePlaceable.width))

        // Place by baselines rather than boxes, so font padding doesn't shift the text.
        val titleBaseline = (TitleSize.toPx() * CapHeightRatio).roundToInt()
        val subtitleBaseline = height - 2.dp.roundToPx()
        layout(width, height) {
            titlePlaceable.place(0, titleBaseline - titlePlaceable[FirstBaseline])
            subtitlePlaceable.place(0, subtitleBaseline - subtitlePlaceable[FirstBaseline])
        }
    }
}
