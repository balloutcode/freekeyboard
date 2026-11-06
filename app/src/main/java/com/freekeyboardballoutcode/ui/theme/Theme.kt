package com.freekeyboardballoutcode.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.freekeyboardballoutcode.data.ThemePreference

@Immutable
data class KeyboardColors(
    val key: Color,
    val keySpecial: Color,
    val keyBorder: Color,
    val onKey: Color,
    val background: Color,
    val shiftActive: Color,
    val onShiftActive: Color,
)

val LocalKeyboardColors = staticCompositionLocalOf {
    KeyboardColors(
        key = LightKey,
        keySpecial = LightKeySpecial,
        keyBorder = LightKeyBorder,
        onKey = LightOnSurface,
        background = LightBackground,
        shiftActive = LightShiftActive,
        onShiftActive = AccentOnPrimary,
    )
}

/** App / settings UI — Light. */
private fun lightScheme(): ColorScheme = lightColorScheme(
    primary = AccentPrimary,
    onPrimary = AccentOnPrimary,
    primaryContainer = AccentContainer,
    onPrimaryContainer = LightOnSurface,
    secondary = AccentPrimary,
    onSecondary = AccentOnPrimary,
    secondaryContainer = AccentContainer,
    onSecondaryContainer = LightOnSurface,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFE0E3EA),
    onSurfaceVariant = Color(0xFF44474F),
    outline = LightKeyBorder,
    outlineVariant = Color(0xFFC5C9D2),
)

/** App / settings UI — classic neutral dark grey. */
private fun darkScheme(): ColorScheme = darkColorScheme(
    primary = AccentPrimaryDark,
    onPrimary = AccentOnPrimaryDark,
    primaryContainer = DarkKeySpecial,
    onPrimaryContainer = DarkOnSurface,
    secondary = Color(0xFFC6C6C6),
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = DarkKey,
    onSecondaryContainer = DarkOnSurface,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkKey,
    onSurfaceVariant = Color(0xFFC4C4C4),
    outline = DarkKeyBorder,
    outlineVariant = Color(0xFF3D3D3D),
)

/** App / settings UI — OLED pure black. */
private fun oledScheme(): ColorScheme = darkColorScheme(
    primary = Color(0xFFE0E0E0),
    onPrimary = Color(0xFF000000),
    primaryContainer = OledKeySpecial,
    onPrimaryContainer = OledOnSurface,
    secondary = Color(0xFFCFCFCF),
    onSecondary = Color(0xFF000000),
    secondaryContainer = OledKey,
    onSecondaryContainer = OledOnSurface,
    background = OledBackground,
    onBackground = OledOnSurface,
    surface = OledSurface,
    onSurface = OledOnSurface,
    surfaceVariant = OledKey,
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = OledKeyBorder,
    outlineVariant = Color(0xFF222222),
)

fun keyboardColorsFor(theme: ThemePreference): KeyboardColors = when (theme) {
    ThemePreference.LIGHT -> KeyboardColors(
        LightKey, LightKeySpecial, LightKeyBorder, LightOnSurface, LightBackground,
        LightShiftActive, AccentOnPrimary,
    )
    ThemePreference.DARK -> KeyboardColors(
        DarkKey, DarkKeySpecial, DarkKeyBorder, DarkOnSurface, DarkBackground,
        DarkShiftActive, AccentOnPrimaryDark,
    )
    ThemePreference.OLED_DARK -> KeyboardColors(
        OledKey, OledKeySpecial, OledKeyBorder, OledOnSurface, OledBackground,
        OledShiftActive, AccentOnPrimaryDark,
    )
}

fun colorSchemeFor(theme: ThemePreference): ColorScheme = when (theme) {
    ThemePreference.LIGHT -> lightScheme()
    ThemePreference.DARK -> darkScheme()
    ThemePreference.OLED_DARK -> oledScheme()
}

@Composable
fun FreekeyboardTheme(
    theme: ThemePreference,
    content: @Composable () -> Unit,
) {
    val colorScheme = colorSchemeFor(theme)
    val keyboardColors = keyboardColorsFor(theme)
    androidx.compose.runtime.CompositionLocalProvider(LocalKeyboardColors provides keyboardColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
