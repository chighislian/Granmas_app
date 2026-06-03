package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreenPrimary,
    onPrimary = DarkGreenOnPrimary,
    primaryContainer = DarkGreenContainer,
    onPrimaryContainer = DarkGreenOnContainer,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    secondary = DarkGreenPrimary,
    onSecondary = DarkGreenOnPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = HealthGreenPrimary,
    onPrimary = HealthGreenOnPrimary,
    primaryContainer = HealthGreenContainer,
    onPrimaryContainer = HealthGreenOnContainer,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    secondary = Color(0xFF2E7D32),
    onSecondary = HealthGreenOnPrimary,
    secondaryContainer = HealthGreenContainer,
    outline = CleanOutlineColor,
    outlineVariant = CleanBorderColor
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
