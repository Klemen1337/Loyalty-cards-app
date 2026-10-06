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

private val TitleSize = 26.sp
private val SubtitleSize = 14.sp

/** Space between the bottom of the title and the top of the subtitle's capitals. */
private val TitleGap = 7.dp

/** Cap height of DM Sans as a share of its font size. */
private const val CapHeightRatio = 0.7f

/**
 * Screen title with a small subtitle close under it, laid out in a block as tall as the
 * header buttons and centered on them.
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
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = SubtitleSize),
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
        val titleCap = TitleSize.toPx() * CapHeightRatio
        val subtitleCap = SubtitleSize.toPx() * CapHeightRatio
        val blockHeight = titleCap + TitleGap.toPx() + subtitleCap
        val top = (height - blockHeight) / 2f
        val titleBaseline = (top + titleCap).roundToInt()
        val subtitleBaseline = (top + blockHeight).roundToInt()
        layout(width, height) {
            titlePlaceable.place(0, titleBaseline - titlePlaceable[FirstBaseline])
            subtitlePlaceable.place(0, subtitleBaseline - subtitlePlaceable[FirstBaseline])
        }
    }
}
