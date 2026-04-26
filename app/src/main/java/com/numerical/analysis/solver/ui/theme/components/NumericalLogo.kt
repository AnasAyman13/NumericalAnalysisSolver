package com.numerical.analysis.solver.ui.theme.components

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.numerical.analysis.solver.ui.theme.PrimaryColor

@Composable
fun NumericalLogo(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    symbolsAlpha: Float = 1f,
    symbolsRotation: Float = 0f,
    glowAlpha: Float = 0f
) {
    val baseBlue = PrimaryColor
    val glowColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = modifier.aspectRatio(1f).padding(20.dp)) {
        val width = size.width
        val height = size.height
        val solutionPoint = Offset(width * 0.8f, height * 0.25f)

        val mainBrush = Brush.linearGradient(
            colors = if (isDarkTheme) listOf(Color.White, Color(0xFFB3E5FC))
            else listOf(baseBlue, baseBlue.copy(alpha = 0.6f))
        )

        // Draw Solution Glow (Only when glowAlpha > 0)
        if (glowAlpha > 0.05f) {
            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = glowColor.copy(alpha = glowAlpha).toArgb()
                    maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.nativeCanvas.drawCircle(solutionPoint.x, solutionPoint.y, 40f * glowAlpha, paint)
            }
        }

        // The Mathematical Core (Integral + Sigma Hybrid)
        val mathPath = Path().apply {
            // Stylized Integral
            moveTo(width * 0.3f, height * 0.2f)
            cubicTo(width * 0.2f, height * 0.1f, width * 0.1f, height * 0.3f, width * 0.2f, height * 0.5f)
            lineTo(width * 0.3f, height * 0.8f)
            cubicTo(width * 0.4f, height * 0.95f, width * 0.2f, height * 0.95f, width * 0.2f, height * 0.85f)

            // Connecting Logic Line (Solution Flow)
            moveTo(width * 0.3f, height * 0.5f)
            quadraticTo(width * 0.5f, height * 0.5f, width * 0.6f, height * 0.35f)

            // Minimalist Sigma
            moveTo(width * 0.6f, height * 0.35f)
            lineTo(width * 0.75f, height * 0.35f)
            lineTo(width * 0.65f, height * 0.45f)
            lineTo(width * 0.75f, height * 0.55f)
            lineTo(width * 0.6f, height * 0.55f)

            // Line to Solution Point
            moveTo(width * 0.75f, height * 0.35f)
            lineTo(solutionPoint.x, solutionPoint.y)
        }

        drawPath(
            path = mathPath,
            brush = mainBrush,
            alpha = symbolsAlpha,
            style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // The Solution Node
        drawCircle(
            color = if (isDarkTheme) Color.White else baseBlue,
            radius = 12f * symbolsAlpha,
            center = solutionPoint,
            alpha = symbolsAlpha
        )
    }
}