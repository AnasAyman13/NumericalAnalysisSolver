package com.numerical.analysis.solver.ui.theme.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun MathBackground(modifier: Modifier = Modifier) {
    val bgColor = Color(0xFFF9FAFB)
    val dotColor = Color.Black.copy(alpha = 0.03f)
    val density = LocalDensity.current
    val stepPx = with(density) { 24.dp.toPx() }
    val dotRadiusPx = with(density) { 1.5.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = bgColor)
                
                var y = stepPx
                while (y < size.height) {
                    var x = stepPx
                    while (x < size.width) {
                        drawCircle(
                            color = dotColor,
                            radius = dotRadiusPx,
                            center = Offset(x, y)
                        )
                        x += stepPx
                    }
                    y += stepPx
                }
            }
    )
}
