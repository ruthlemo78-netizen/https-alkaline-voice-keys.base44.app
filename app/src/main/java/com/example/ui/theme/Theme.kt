package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AlkalineDarkColorScheme = darkColorScheme(
    primary = VegaEmerald,
    onPrimary = AlkalineObsidian,
    secondary = AlkalineTeal,
    onSecondary = AlkalineObsidian,
    tertiary = DexAmber,
    onTertiary = AlkalineObsidian,
    background = AlkalineObsidian,
    onBackground = TextPrimary,
    surface = AlkalineCharcoal,
    onSurface = TextPrimary,
    surfaceVariant = AlkalinePanelSolid,
    onSurfaceVariant = TextSecondary,
    outline = AlkalineBorder,
    error = WantedPoliceRed,
    onError = TextPrimary
)

@Composable
fun AlkalineCityTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AlkalineDarkColorScheme,
        typography = AlkalineTypography,
        content = content
    )
}
