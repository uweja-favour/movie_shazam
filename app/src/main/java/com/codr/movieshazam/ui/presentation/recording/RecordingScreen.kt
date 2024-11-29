package com.codr.movieshazam.ui.presentation.recording

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codr.movieshazam.ui.theme.LARGE_PADDING


@Composable
fun RecordingScreen(onStopRecording: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = LARGE_PADDING.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Animated pulsating effect
        PulsatingRecordingAnimation(onStopRecording = onStopRecording)

        // Microphone icon
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Recording Indicator",
            modifier = Modifier.size(80.dp),
            tint = Color.Red
        )

        Text(
            text = "Recording...",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Gray
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}


@Composable
fun PulsatingRecordingAnimation(onStopRecording: () -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    // Infinite transition for pulsating animation
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .size((screenWidth * .8).dp)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsating circle
        Canvas(modifier = Modifier.size(200.dp)) {
            drawCircle(
                color = Color.Red,
                radius = size.minDimension / 2 * scale,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Inner solid circle
        Canvas(
            modifier = Modifier.size(100.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onStopRecording()
                        }
                    )
                }
        ) {
            drawCircle(color = Color.Red)
        }
    }
}