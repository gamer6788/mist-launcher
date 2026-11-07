
package com.mist.launcher

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SingularityIntro(
    onFinished: () -> Unit,
    onSkip: () -> Unit = onFinished
) {
    val progress = remember {
        Animatable(0f)
    }

    var finished by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 2400
            )
        )

        delay(450)

        if (!finished) {
            finished = true
            onFinished()
        }
    }

    val reveal = (progress.value - 0.55f)
        .coerceIn(0f, 0.45f) / 0.45f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = Offset(
                x = size.width / 2f,
                y = size.height * 0.43f
            )

            val maxRadius = size.width * 0.43f
            val radius = maxRadius *
                progress.value.coerceAtLeast(0.025f)

            // Atmospheric cosmic glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x664B24A0),
                        Color(0x303A9DC8),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 1.7f
                ),
                radius = maxRadius * 1.7f,
                center = center
            )

            // Outer gravitational ring
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF7652E8),
                        Color(0xFF65DFFF),
                        Color(0xFFE9DCFF),
                        Color(0xFF7952E9),
                        Color.Transparent
                    ),
                    center = center
                ),
                startAngle = -100f + progress.value * 180f,
                sweepAngle = 310f,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius * 1.25f,
                    center.y - radius * 0.48f
                ),
                size = Size(
                    radius * 2.5f,
                    radius * 0.96f
                ),
                style = Stroke(
                    width = (radius * 0.11f)
                        .coerceAtLeast(1f)
                )
            )

            // Inner luminous ring
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF8E65FF),
                        Color(0xFF4ACDFF),
                        Color(0xFFE7E0FF),
                        Color.Transparent
                    ),
                    center = center
                ),
                radius = radius * 0.78f,
                center = center,
                style = Stroke(
                    width = (radius * 0.045f)
                        .coerceAtLeast(1f)
                )
            )

            // Event horizon shadow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF030208),
                        Color(0xFF170D27)
                    ),
                    center = center,
                    radius = radius * 0.9f
                ),
                radius = radius * 0.75f,
                center = center
            )

            // Central black hole
            drawCircle(
                color = Color.Black,
                radius = radius * 0.63f,
                center = center
            )

            // Subtle event horizon edge
            drawCircle(
                color = Color(0xFF7652C8)
                    .copy(alpha = 0.45f * progress.value),
                radius = radius * 0.65f,
                center = center,
                style = Stroke(
                    width = (radius * 0.018f)
                        .coerceAtLeast(1f)
                )
            )
        }

        // Elegant MIST wordmark
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 125.dp)
                .alpha(reveal),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "MIST",
                color = Color(0xFFECE7FA),
                fontSize = 38.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
                letterSpacing = 12.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "BEYOND THE ORDINARY",
                color = Color(0xFF9D91BC),
                fontSize = 9.sp,
                letterSpacing = 4.sp
            )
        }

        // Skip control
        Text(
            text = "SKIP",
            color = Color(0xFFB8B0CC),
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(22.dp)
                .clickable {
                    if (!finished) {
                        finished = true
                        onSkip()
                    }
                }
                .padding(8.dp)
        )
    }
}
