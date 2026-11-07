package com.mist.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MistExperienceSettings(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var preferences by remember {
        mutableStateOf(MistPreferences.load(context))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "MIST EXPERIENCE",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 21.sp
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Control how MIST opens and how its motion feels.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                fontSize = 12.sp
            )
        }

        ExperienceSettingCard(
            title = "Singularity opening",
            description = "Show the animated black-hole introduction when MIST opens.",
            checked = preferences.introEnabled,
            onCheckedChange = { enabled ->
                MistPreferences.saveIntroEnabled(context, enabled)
                preferences = preferences.copy(introEnabled = enabled)
            }
        )

        ExperienceSettingCard(
            title = "Reduced motion",
            description = "Reduce animated movement and visual transitions.",
            checked = preferences.reducedMotion,
            onCheckedChange = { enabled ->
                MistPreferences.saveReducedMotion(context, enabled)
                preferences = preferences.copy(reducedMotion = enabled)
            }
        )

        Text(
            text = "These settings are saved automatically.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun ExperienceSettingCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    fontSize = 11.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}