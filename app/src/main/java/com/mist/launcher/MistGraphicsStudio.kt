package com.mist.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MistGraphicsStudio(
    selectedMode: MistGraphicsMode,
    onModeSelected: (MistGraphicsMode) -> Unit,
    glowEnabled: Boolean,
    onGlowChanged: (Boolean) -> Unit,
    backgroundAnimationEnabled: Boolean,
    onBackgroundAnimationChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "GRAPHICS STUDIO",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Shape the visual intensity of your MIST experience.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                fontSize = 13.sp
            )
        }

        MistGraphicsMode.entries.forEach { mode ->
            GraphicsModeCard(
                mode = mode,
                selected = mode == selectedMode,
                onClick = { onModeSelected(mode) }
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "VISUAL EFFECTS",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        GraphicsToggle(
            title = "Atmospheric glow",
            description = "Enable subtle light and glow effects.",
            checked = glowEnabled,
            onCheckedChange = onGlowChanged
        )

        GraphicsToggle(
            title = "Animated background",
            description = "Allow background effects to move.",
            checked = backgroundAnimationEnabled,
            onCheckedChange = onBackgroundAnimationChanged
        )

        Text(
            text = "Graphics mode and theme can be changed independently. "
                + "More intense effects may use additional battery and memory.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun GraphicsModeCard(
    mode: MistGraphicsMode,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = when (mode) {
        MistGraphicsMode.WHISPER -> Color(0xFF8D9AA8)
        MistGraphicsMode.PHANTOM -> Color(0xFF9B83E8)
        MistGraphicsMode.ETHEREAL -> Color(0xFF64C9D5)
        MistGraphicsMode.SPECTRAL -> Color(0xFFCF8BEF)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (selected) {
                    Modifier.border(
                        1.5.dp,
                        accent,
                        RoundedCornerShape(20.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(43.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f))
                    .border(1.dp, accent.copy(alpha = 0.65f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(
                            when (mode) {
                                MistGraphicsMode.WHISPER -> 8.dp
                                MistGraphicsMode.PHANTOM -> 14.dp
                                MistGraphicsMode.ETHEREAL -> 21.dp
                                MistGraphicsMode.SPECTRAL -> 27.dp
                            }
                        )
                        .clip(CircleShape)
                        .background(accent)
                )
            }

            Spacer(Modifier.size(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mode.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = mode.description,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp
                )
            }

            if (selected) {
                Text(
                    text = "✓",
                    color = accent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GraphicsToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = description,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f),
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}