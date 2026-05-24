package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WaynessLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val radius = w / 2

        // 1. Draw Linear Purple-to-Pink Gradient Circle
        val gradient = Brush.linearGradient(
            colors = listOf(Color(0xFF8E24AA), Color(0xFFE91E63)),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )
        drawCircle(
            brush = gradient,
            radius = radius,
            center = Offset(radius, radius)
        )

        // 2. Draw outer silver/light-pink ring
        drawCircle(
            color = Color(0x33FFFFFF),
            radius = radius - 2,
            center = Offset(radius, radius),
            style = Stroke(width = w * 0.04f)
        )

        // Calculate proportions relative to canvas size
        val cX = w / 2
        val cY = h / 2
        val scale = w / 100f

        // 3. Draw Dumbbell weight plates
        // Left small outer plate
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cX - 38 * scale, cY - 8 * scale),
            size = Size(3.5f * scale, 16 * scale),
            cornerRadius = CornerRadius(1.75f * scale, 1.75f * scale)
        )
        // Left medium inner plate
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cX - 32 * scale, cY - 18 * scale),
            size = Size(4.5f * scale, 36 * scale),
            cornerRadius = CornerRadius(2.25f * scale, 2.25f * scale)
        )

        // Right medium inner plate
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cX + 27.5f * scale, cY - 18 * scale),
            size = Size(4.5f * scale, 36 * scale),
            cornerRadius = CornerRadius(2.25f * scale, 2.25f * scale)
        )
        // Right small outer plate
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cX + 34.5f * scale, cY - 8 * scale),
            size = Size(3.5f * scale, 16 * scale),
            cornerRadius = CornerRadius(1.75f * scale, 1.75f * scale)
        )

        // 4. Draw central "W" shaped bar spanning from the dumbbell inner plates
        // Left horizontal dumbbell rod hook
        drawLine(
            color = Color.White,
            start = Offset(cX - 32 * scale, cY),
            end = Offset(cX - 22 * scale, cY),
            strokeWidth = 6.5f * scale,
            cap = StrokeCap.Round
        )
        // Right horizontal dumbbell rod hook
        drawLine(
            color = Color.White,
            start = Offset(cX + 22 * scale, cY),
            end = Offset(cX + 32 * scale, cY),
            strokeWidth = 6.5f * scale,
            cap = StrokeCap.Round
        )

        // Draw modern smooth W shape using canvas lines
        // Left valley
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cX - 25 * scale, cY - 18 * scale),
            size = Size(7.5f * scale, 34 * scale),
            cornerRadius = CornerRadius(3.75f * scale, 3.75f * scale)
        )
        // Right valley
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cX + 17.5f * scale, cY - 18 * scale),
            size = Size(7.5f * scale, 34 * scale),
            cornerRadius = CornerRadius(3.75f * scale, 3.75f * scale)
        )
        // Center middle bump
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cX - 5 * scale, cY - 5 * scale),
            size = Size(10 * scale, 23 * scale),
            cornerRadius = CornerRadius(5f * scale, 5f * scale)
        )

        // Cutouts/connecting paths drawn to look like a contiguous fenix letter-dumbbell
        // bottom connection bars
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cX - 22 * scale, cY + 8 * scale),
            size = Size(44 * scale, 10 * scale),
            cornerRadius = CornerRadius(4f * scale, 4f * scale)
        )
    }
}
