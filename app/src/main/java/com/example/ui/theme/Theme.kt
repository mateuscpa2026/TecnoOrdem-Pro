package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BlueAccent,
    onPrimary = NavyDeep,
    primaryContainer = BluePrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = BlueAccent,
    onSecondary = NavyDeep,
    background = NavyDeep,
    onBackground = TextPrimaryDark,
    surface = NavyDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = NavyDarkCard,
    onSurfaceVariant = TextSecondaryDark,
    error = StatusCritical,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueSoft,
    onPrimaryContainer = NavyDeep,
    secondary = BlueAccent,
    onSecondary = NavyDeep,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = TextSecondaryLight,
    error = StatusCritical,
    onError = Color.White
)

@Composable
fun TecnoOrdemTheme(
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

// Backwards-compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    TecnoOrdemTheme(darkTheme = darkTheme, content = content)
}
