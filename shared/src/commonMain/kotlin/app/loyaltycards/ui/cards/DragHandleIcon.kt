package app.loyaltycards.ui.cards

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Material "drag_handle" glyph, kept local to avoid pulling in the extended icon set. */
internal val DragHandleIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "DragHandle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20f, 9f)
            horizontalLineTo(4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(16f)
            close()
            moveTo(4f, 15f)
            horizontalLineToRelative(16f)
            verticalLineToRelative(-2f)
            horizontalLineTo(4f)
            close()
        }
    }.build()
}
