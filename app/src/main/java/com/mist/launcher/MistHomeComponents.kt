
package com.mist.launcher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SingularityAtmosphere(
    palette: MistPalette,
    animationEnabled: Boolean
) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val center = Offset(
            x = size.width * 0.5f,
            y = size.height * 0.42f
        )

        val radius = size.minDimension * 0.28f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.primary.copy(alpha = 0.16f),
                    palette.secondary.copy(alpha = 0.07f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 2.4f
            ),
            radius = radius * 2.4f,
            center = center
        )

        drawCircle(
            color = Color.Black.copy(alpha = 0.75f),
            radius = radius * 0.65f,
            center = center
        )

        drawCircle(
            color = palette.primary.copy(alpha = 0.35f),
            radius = radius * 0.82f,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp.toPx()
            )
        )

        if (animationEnabled) {
            drawOval(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        palette.primary.copy(alpha = 0.65f),
                        palette.secondary.copy(alpha = 0.8f),
                        Color.Transparent,
                        palette.primary.copy(alpha = 0.5f)
                    ),
                    center = center
                ),
                topLeft = Offset(
                    center.x - radius * 1.3f,
                    center.y - radius * 0.38f
                ),
                size = Size(
                    radius * 2.6f,
                    radius * 0.76f
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 3.dp.toPx()
                )
            )
        }
    }
}

@Composable
fun HeaderAction(
    symbol: String,
    onClick: () -> Unit
) {
    SmallAction(symbol, onClick)
}

@Composable
fun ScreenHeader(
    title: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallAction("‹", onBack)

        Spacer(Modifier.width(16.dp))

        Text(
            title.uppercase(),
            fontSize = 13.sp,
            letterSpacing = 2.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SettingsPage(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(top = 0.dp)
    ) {
        ScreenHeader(title, onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 20.dp),
            content = content
        )
    }
}

@Composable
fun SmallAction(
    symbol: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = CircleShape,
        modifier = Modifier
            .size(42.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                symbol,
                color = Color.White,
                fontSize = 21.sp
            )
        }
    }
}

@Composable
fun HomeButton(
    title: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.07f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                title,
                color = Color.White,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
