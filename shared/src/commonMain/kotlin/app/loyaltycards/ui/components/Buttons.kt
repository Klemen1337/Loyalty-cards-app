package app.loyaltycards.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PillStyle { Filled, Outlined, Destructive }

/** Rounded text button from the design: black, outlined white, or red. */
@Composable
internal fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PillStyle = PillStyle.Filled,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
) {
    val colors = MaterialTheme.colorScheme
    when (style) {
        PillStyle.Outlined -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = CircleShape,
            border = BorderStroke(1.dp, colors.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = colors.surface,
                contentColor = colors.onSurface,
            ),
            contentPadding = contentPadding,
        ) { Text(text, style = MaterialTheme.typography.labelLarge) }

        else -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = CircleShape,
            colors = if (style == PillStyle.Destructive) {
                ButtonDefaults.buttonColors(containerColor = colors.error, contentColor = colors.onError)
            } else {
                ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary)
            },
            contentPadding = contentPadding,
        ) { Text(text, style = MaterialTheme.typography.labelLarge) }
    }
}

/** Round icon button: outlined white by default, black when [filled]. */
@Composable
internal fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    size: Dp = 48.dp,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = CircleShape,
        color = if (filled) colors.primary else colors.surface,
        contentColor = if (filled) colors.onPrimary else colors.onSurface,
        border = if (filled) null else BorderStroke(1.dp, colors.outline),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = contentDescription)
        }
    }
}
