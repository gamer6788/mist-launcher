package com.mist.launcher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun MistAppearanceScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var preferences by remember {
        mutableStateOf(MistPreferences.load(context))
    }

    var glowEnabled by remember {
        mutableStateOf(true)
    }

    var backgroundAnimationEnabled by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "PERSONALISE MIST",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Your atmosphere. Your visual intensity. Your MIST.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            style = MaterialTheme.typography.bodySmall
        )

        MistThemeStudio(
            selectedTheme = preferences.theme,
            onThemeSelected = { theme ->
                MistPreferences.saveTheme(context, theme)

                preferences = preferences.copy(
                    theme = theme
                )
            }
        )

        Spacer(Modifier.height(8.dp))

        MistGraphicsStudio(
            selectedMode = preferences.graphicsMode,
            onModeSelected = { mode ->
                MistPreferences.saveGraphicsMode(context, mode)

                preferences = preferences.copy(
                    graphicsMode = mode
                )
            },
            glowEnabled = glowEnabled,
            onGlowChanged = { glowEnabled = it },
            backgroundAnimationEnabled = backgroundAnimationEnabled,
            onBackgroundAnimationChanged = {
                backgroundAnimationEnabled = it
            }
        )

        Spacer(Modifier.height(24.dp))
    }
}