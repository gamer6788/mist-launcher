package com.mist.launcher

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MistThemeProvider(
    theme: MistTheme,
    content: @Composable () -> Unit
) {
    val palette = MistDesignSystem.paletteFor(theme)

    val colorScheme = darkColorScheme(
        primary = palette.primary,
        onPrimary = palette.background,

        secondary = palette.secondary,
        onSecondary = palette.background,

        tertiary = palette.accent,
        onTertiary = palette.background,

        background = palette.background,
        onBackground = palette.textPrimary,

        surface = palette.surface,
        onSurface = palette.textPrimary,

        surfaceVariant = palette.surfaceElevated,
        onSurfaceVariant = palette.textSecondary,

        outline = palette.secondary.copy(alpha = 0.55f)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}