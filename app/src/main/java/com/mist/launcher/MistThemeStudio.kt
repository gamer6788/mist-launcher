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
fun MistThemeStudio(
    selectedTheme: MistTheme,
    onThemeSelected: (MistTheme) -> Unit,
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
                text = "THEME STUDIO",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Choose the atmosphere of your MIST experience.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                fontSize = 13.sp
            )
        }

        MistTheme.entries.forEach { theme ->
            MistThemeCard(
                theme = theme,
                selected = theme == selectedTheme,
                onClick = { onThemeSelected(theme) }
            )
        }

        Text(
            text = "Your theme choice is independent of your graphics mode.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

@Composable
private fun MistThemeCard(
    theme: MistTheme,
    selected: Boolean,
    onClick: () -> Unit
) {
    val palette = MistDesignSystem.paletteFor(theme)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = palette.primary,
                        shape = RoundedCornerShape(22.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = palette.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 5.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = theme.title,
                        color = palette.textPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = theme.description,
                        color = palette.textSecondary,
                        fontSize = 12.sp
                    )
                }

                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(27.dp)
                            .clip(CircleShape)
                            .background(palette.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = palette.background,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Theme preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(palette.background)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThemePreviewOrb(
                        color = palette.primary,
                        size = 43.dp
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 92.dp, height = 7.dp)
                                .clip(CircleShape)
                                .background(palette.textPrimary.copy(alpha = 0.85f))
                        )

                        Box(
                            modifier = Modifier
                                .size(width = 62.dp, height = 5.dp)
                                .clip(CircleShape)
                                .background(palette.textSecondary.copy(alpha = 0.7f))
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ThemeColorDot(palette.primary)
                    ThemeColorDot(palette.secondary)
                    ThemeColorDot(palette.accent)
                }
            }

            if (selected) {
                Spacer(Modifier.height(10.dp))

                Text(
                    text = "CURRENTLY SELECTED",
                    color = palette.primary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

@Composable
private fun ThemePreviewOrb(
    color: Color,
    size: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.18f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.9f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.42f)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
private fun ThemeColorDot(color: Color) {
    Box(
        modifier = Modifier
            .size(13.dp)
            .clip(CircleShape)
            .background(color)
    )
}