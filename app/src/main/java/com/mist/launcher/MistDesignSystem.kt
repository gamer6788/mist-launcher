
package com.mist.launcher

import androidx.compose.ui.graphics.Color

/**
 * MIST V1.3 Design Foundation
 *
 * Themes control the visual identity.
 * Graphics modes independently control visual intensity.
 */

enum class MistTheme(
    val title: String,
    val description: String
) {
    SINGULARITY(
        "Singularity",
        "Black hole, cosmic light and gravitational depth"
    ),

    AURORA_NOCTURNE(
        "Aurora Nocturne",
        "Teal, cyan and ultraviolet auroras"
    ),

    CELESTIAL_ECLIPSE(
        "Celestial Eclipse",
        "Amethyst, silver and celestial darkness"
    ),

    ENCHANTED_MIST(
        "Enchanted Mist",
        "Moonlit forests and silver atmospheric fog"
    )
}

enum class MistGraphicsMode(
    val title: String,
    val description: String,
    val glowStrength: Float,
    val transparency: Float,
    val animationEnabled: Boolean
) {
    WHISPER(
        "Whisper",
        "Minimal effects and maximum restraint",
        0.10f,
        0.04f,
        false
    ),

    PHANTOM(
        "Phantom",
        "Balanced glass, glow and transitions",
        0.35f,
        0.10f,
        true
    ),

    ETHEREAL(
        "Ethereal",
        "Rich atmosphere and luminous depth",
        0.65f,
        0.17f,
        true
    ),

    SPECTRAL(
        "Spectral",
        "Maximum available visual effects",
        1.0f,
        0.24f,
        true
    )
}

data class MistPalette(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val glow: Color
)

object MistDesignSystem {

    val palettes: Map<MistTheme, MistPalette> = mapOf(

        MistTheme.SINGULARITY to MistPalette(
            background = Color(0xFF050509),
            surface = Color(0xFF11111B),
            surfaceElevated = Color(0xFF1B1928),
            primary = Color(0xFF9B7BFF),
            secondary = Color(0xFF4BCBFF),
            accent = Color(0xFFE6D9FF),
            textPrimary = Color(0xFFF5F1FF),
            textSecondary = Color(0xFFA7A0BB),
            glow = Color(0xFF8054FF)
        ),

        MistTheme.AURORA_NOCTURNE to MistPalette(
            background = Color(0xFF061014),
            surface = Color(0xFF102127),
            surfaceElevated = Color(0xFF193039),
            primary = Color(0xFF42DCC8),
            secondary = Color(0xFF52BFFF),
            accent = Color(0xFF9B8AFF),
            textPrimary = Color(0xFFE9FFFF),
            textSecondary = Color(0xFF9CBBC1),
            glow = Color(0xFF32DCCB)
        ),

        MistTheme.CELESTIAL_ECLIPSE to MistPalette(
            background = Color(0xFF0B0815),
            surface = Color(0xFF191327),
            surfaceElevated = Color(0xFF251C36),
            primary = Color(0xFFB28BFF),
            secondary = Color(0xFFD6C5FF),
            accent = Color(0xFFEA8BDA),
            textPrimary = Color(0xFFF6F0FF),
            textSecondary = Color(0xFFB5A8CA),
            glow = Color(0xFF9665E8)
        ),

        MistTheme.ENCHANTED_MIST to MistPalette(
            background = Color(0xFF07100F),
            surface = Color(0xFF13201D),
            surfaceElevated = Color(0xFF1C302A),
            primary = Color(0xFF9BCBB1),
            secondary = Color(0xFFA8C9E8),
            accent = Color(0xFFD8E8E0),
            textPrimary = Color(0xFFF0F8F2),
            textSecondary = Color(0xFFA5BDB0),
            glow = Color(0xFF79B99C)
        )
    )

    fun paletteFor(theme: MistTheme): MistPalette {
        return palettes[theme] ?: palettes.getValue(
            MistTheme.SINGULARITY
        )
    }

    val defaultTheme = MistTheme.SINGULARITY

    val defaultGraphicsMode = MistGraphicsMode.PHANTOM
}
