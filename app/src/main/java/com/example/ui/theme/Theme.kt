package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WaynessPrimary,
    secondary = WaynessSecondary,
    tertiary = WaynessAccent,
    background = BoldBg,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = BoldLavenderText,
    onTertiary = BoldBlueText,
    onBackground = BoldTextPrimary,
    onSurface = BoldTextPrimary,
    surfaceVariant = BoldNavBg,
    onSurfaceVariant = BoldTextSecondary,
    outline = BoldOutline
)

private val LightColorScheme = lightColorScheme(
    primary = WaynessPrimary,
    secondary = WaynessSecondary,
    tertiary = WaynessAccent,
    background = BoldBg,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = BoldLavenderText,
    onTertiary = BoldBlueText,
    onBackground = BoldTextPrimary,
    onSurface = BoldTextPrimary,
    surfaceVariant = BoldNavBg,
    onSurfaceVariant = BoldTextSecondary,
    outline = BoldOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Both mapped to the punchy "Bold Typography" theme for consistent visual identity
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
