package com.mist.launcher

import androidx.compose.ui.graphics.Color

data class MistGraphicsProfile(
    val glowStrength: Float,
    val glassTransparency: Float,
    val backgroundMotion: Float,
    val transitionDuration: Int,
    val iconGlow: Float,
    val blurEnabled: Boolean,
    val particleEffects: Boolean
)

object MistGraphicsEngine {

    fun profileFor(
        mode: MistGraphicsMode,
        reducedMotion: Boolean = false,
        glowEnabled: Boolean = true,
        animationEnabled: Boolean = true
    ): MistGraphicsProfile {

        val base = when (mode) {

            MistGraphicsMode.WHISPER ->
                MistGraphicsProfile(
                    glowStrength = 0.10f,
                    glassTransparency = 0.96f,
                    backgroundMotion = 0.0f,
                    transitionDuration = 120,
                    iconGlow = 0.0f,
                    blurEnabled = false,
                    particleEffects = false
                )

            MistGraphicsMode.PHANTOM ->
                MistGraphicsProfile(
                    glowStrength = 0.35f,
                    glassTransparency = 0.82f,
                    backgroundMotion = 0.35f,
                    transitionDuration = 220,
                    iconGlow = 0.20f,
                    blurEnabled = false,
                    particleEffects = false
                )

            MistGraphicsMode.ETHEREAL ->
                MistGraphicsProfile(
                    glowStrength = 0.65f,
                    glassTransparency = 0.70f,
                    backgroundMotion = 0.65f,
                    transitionDuration = 320,
                    iconGlow = 0.45f,
                    blurEnabled = true,
                    particleEffects = true
                )

            MistGraphicsMode.SPECTRAL ->
                MistGraphicsProfile(
                    glowStrength = 1.0f,
                    glassTransparency = 0.55f,
                    backgroundMotion = 1.0f,
                    transitionDuration = 420,
                    iconGlow = 0.80f,
                    blurEnabled = true,
                    particleEffects = true
                )
        }

        return base.copy(
            glowStrength = if (glowEnabled) {
                base.glowStrength
            } else {
                0f
            },
            backgroundMotion = if (
                reducedMotion || !animationEnabled
            ) {
                0f
            } else {
                base.backgroundMotion
            },
            transitionDuration = if (reducedMotion) {
                0
            } else {
                base.transitionDuration
            },
            iconGlow = if (glowEnabled) {
                base.iconGlow
            } else {
                0f
            },
            blurEnabled = base.blurEnabled && !reducedMotion,
            particleEffects = base.particleEffects &&
                !reducedMotion &&
                animationEnabled
        )
    }
}