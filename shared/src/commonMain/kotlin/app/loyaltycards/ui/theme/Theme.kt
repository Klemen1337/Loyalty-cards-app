package app.loyaltycards.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.loyaltycards.resources.Res
import app.loyaltycards.resources.dm_sans_bold
import app.loyaltycards.resources.dm_sans_display_bold
import app.loyaltycards.resources.dm_sans_medium
import app.loyaltycards.resources.dm_sans_regular
import app.loyaltycards.resources.dm_sans_semibold
import org.jetbrains.compose.resources.Font

private val LightColors = lightColorScheme(
    primary = Color(0xFF16171A),
    onPrimary = Color.White,
    background = Color(0xFFF2F0EB),
    onBackground = Color(0xFF16171A),
    surface = Color.White,
    onSurface = Color(0xFF16171A),
    surfaceContainerLow = Color(0xFFF7F6F2),
    surfaceContainerHigh = Color.White,
    onSurfaceVariant = Color(0xFF6E6C67),
    outline = Color(0xFFD9D6CE),
    outlineVariant = Color(0xFFE4E1DA),
    error = Color(0xFFB42318),
    onError = Color.White,
    errorContainer = Color(0xFFFBE4E1),
    scrim = Color(0xFF16171A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFF2F0EB),
    onPrimary = Color(0xFF16171A),
    background = Color(0xFF121315),
    onBackground = Color(0xFFF2F0EB),
    surface = Color(0xFF1E1F22),
    onSurface = Color(0xFFF2F0EB),
    surfaceContainerLow = Color(0xFF1A1B1E),
    surfaceContainerHigh = Color(0xFF26272B),
    onSurfaceVariant = Color(0xFFA3A19B),
    outline = Color(0xFF3A3B3F),
    outlineVariant = Color(0xFF2E2F33),
    error = Color(0xFFF97066),
    onError = Color(0xFF16171A),
    errorContainer = Color(0xFF3B1714),
    scrim = Color.Black,
)

/** Page background behind the enlarged card. */
val EnlargedCardBackdrop = Color(0xFF141518)

@Composable
private fun appTypography(): Typography {
    val text = FontFamily(
        Font(Res.font.dm_sans_regular, FontWeight.Normal),
        Font(Res.font.dm_sans_medium, FontWeight.Medium),
        Font(Res.font.dm_sans_semibold, FontWeight.SemiBold),
        Font(Res.font.dm_sans_bold, FontWeight.Bold),
    )
    val display = FontFamily(Font(Res.font.dm_sans_display_bold, FontWeight.Bold))
    val base = Typography()
    return Typography(
        displaySmall = TextStyle(fontFamily = display, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 40.sp, letterSpacing = (-0.03).em),
        headlineMedium = TextStyle(fontFamily = display, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.02).em),
        headlineSmall = TextStyle(fontFamily = display, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = (-0.01).em),
        titleLarge = TextStyle(fontFamily = display, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
        titleMedium = base.titleMedium.copy(fontFamily = text, fontWeight = FontWeight.Bold),
        titleSmall = base.titleSmall.copy(fontFamily = text, fontWeight = FontWeight.SemiBold),
        bodyLarge = base.bodyLarge.copy(fontFamily = text),
        bodyMedium = base.bodyMedium.copy(fontFamily = text),
        bodySmall = base.bodySmall.copy(fontFamily = text),
        labelLarge = base.labelLarge.copy(fontFamily = text, fontWeight = FontWeight.Bold, fontSize = 16.sp),
        labelMedium = base.labelMedium.copy(fontFamily = text, fontWeight = FontWeight.Medium, letterSpacing = 0.08.em),
        labelSmall = base.labelSmall.copy(fontFamily = text),
    )
}

@Composable
fun LoyaltyCardsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = appTypography(),
        content = content,
    )
}
