
package com.mist.launcher

import android.content.Context

data class MistUserPreferences(
    val theme: MistTheme = MistTheme.SINGULARITY,
    val graphicsMode: MistGraphicsMode = MistGraphicsMode.PHANTOM,
    val introEnabled: Boolean = true,
    val reducedMotion: Boolean = false,
    val glowEnabled: Boolean = true,
    val backgroundAnimationEnabled: Boolean = true
)

object MistPreferences {

    private const val PREFS_NAME = "mist_user_preferences"

    private const val KEY_THEME = "theme"
    private const val KEY_GRAPHICS_MODE = "graphics_mode"
    private const val KEY_INTRO_ENABLED = "intro_enabled"
    private const val KEY_REDUCED_MOTION = "reduced_motion"
    private const val KEY_GLOW_ENABLED = "glow_enabled"
    private const val KEY_BACKGROUND_ANIMATION = "background_animation"

    private fun prefs(context: Context) =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    fun load(context: Context): MistUserPreferences {
        val preferences = prefs(context)

        val theme = runCatching {
            MistTheme.valueOf(
                preferences.getString(
                    KEY_THEME,
                    MistTheme.SINGULARITY.name
                )!!
            )
        }.getOrDefault(MistTheme.SINGULARITY)

        val graphicsMode = runCatching {
            MistGraphicsMode.valueOf(
                preferences.getString(
                    KEY_GRAPHICS_MODE,
                    MistGraphicsMode.PHANTOM.name
                )!!
            )
        }.getOrDefault(MistGraphicsMode.PHANTOM)

        return MistUserPreferences(
            theme = theme,
            graphicsMode = graphicsMode,
            introEnabled = preferences.getBoolean(
                KEY_INTRO_ENABLED,
                true
            ),
            reducedMotion = preferences.getBoolean(
                KEY_REDUCED_MOTION,
                false
            ),
            glowEnabled = preferences.getBoolean(
                KEY_GLOW_ENABLED,
                true
            ),
            backgroundAnimationEnabled = preferences.getBoolean(
                KEY_BACKGROUND_ANIMATION,
                true
            )
        )
    }

    fun saveTheme(context: Context, theme: MistTheme) {
        prefs(context).edit()
            .putString(KEY_THEME, theme.name)
            .apply()
    }

    fun saveGraphicsMode(
        context: Context,
        mode: MistGraphicsMode
    ) {
        prefs(context).edit()
            .putString(KEY_GRAPHICS_MODE, mode.name)
            .apply()
    }

    fun saveIntroEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit()
            .putBoolean(KEY_INTRO_ENABLED, enabled)
            .apply()
    }

    fun saveReducedMotion(context: Context, enabled: Boolean) {
        prefs(context).edit()
            .putBoolean(KEY_REDUCED_MOTION, enabled)
            .apply()
    }

    fun saveGlowEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit()
            .putBoolean(KEY_GLOW_ENABLED, enabled)
            .apply()
    }

    fun saveBackgroundAnimationEnabled(
        context: Context,
        enabled: Boolean
    ) {
        prefs(context).edit()
            .putBoolean(KEY_BACKGROUND_ANIMATION, enabled)
            .apply()
    }

    fun reset(context: Context) {
        prefs(context).edit().clear().apply()
    }
}