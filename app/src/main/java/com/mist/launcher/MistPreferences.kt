
package com.mist.launcher

import android.content.Context

data class MistUserPreferences(
    val theme: MistTheme = MistTheme.SINGULARITY,
    val graphicsMode: MistGraphicsMode = MistGraphicsMode.PHANTOM,
    val introEnabled: Boolean = true,
    val reducedMotion: Boolean = false
)

object MistPreferences {

    private const val PREFS_NAME = "mist_user_preferences"

    private const val KEY_THEME = "selected_theme"
    private const val KEY_GRAPHICS = "selected_graphics"
    private const val KEY_INTRO = "intro_enabled"
    private const val KEY_REDUCED_MOTION = "reduced_motion"

    private fun preferences(context: Context) =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    /**
     * Load all saved MIST preferences.
     */
    fun load(context: Context): MistUserPreferences {
        val prefs = preferences(context)

        val themeName = prefs.getString(
            KEY_THEME,
            MistTheme.SINGULARITY.name
        )

        val graphicsName = prefs.getString(
            KEY_GRAPHICS,
            MistGraphicsMode.PHANTOM.name
        )

        val theme = runCatching {
            MistTheme.valueOf(
                themeName ?: MistTheme.SINGULARITY.name
            )
        }.getOrDefault(MistTheme.SINGULARITY)

        val graphics = runCatching {
            MistGraphicsMode.valueOf(
                graphicsName ?: MistGraphicsMode.PHANTOM.name
            )
        }.getOrDefault(MistGraphicsMode.PHANTOM)

        return MistUserPreferences(
            theme = theme,
            graphicsMode = graphics,
            introEnabled = prefs.getBoolean(
                KEY_INTRO,
                true
            ),
            reducedMotion = prefs.getBoolean(
                KEY_REDUCED_MOTION,
                false
            )
        )
    }

    /**
     * Save selected theme.
     */
    fun saveTheme(
        context: Context,
        theme: MistTheme
    ) {
        preferences(context)
            .edit()
            .putString(KEY_THEME, theme.name)
            .apply()
    }

    /**
     * Save graphics intensity independently.
     */
    fun saveGraphicsMode(
        context: Context,
        mode: MistGraphicsMode
    ) {
        preferences(context)
            .edit()
            .putString(KEY_GRAPHICS, mode.name)
            .apply()
    }

    /**
     * Enable or disable the Singularity opening animation.
     */
    fun saveIntroEnabled(
        context: Context,
        enabled: Boolean
    ) {
        preferences(context)
            .edit()
            .putBoolean(KEY_INTRO, enabled)
            .apply()
    }

    /**
     * Save reduced-motion preference.
     */
    fun saveReducedMotion(
        context: Context,
        enabled: Boolean
    ) {
        preferences(context)
            .edit()
            .putBoolean(KEY_REDUCED_MOTION, enabled)
            .apply()
    }

    /**
     * Reset all preferences to MIST defaults.
     */
    fun reset(context: Context) {
        preferences(context)
            .edit()
            .clear()
            .apply()
    }
}
