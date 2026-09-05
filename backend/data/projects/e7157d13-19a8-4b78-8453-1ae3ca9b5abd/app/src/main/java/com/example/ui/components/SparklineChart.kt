package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.Sprout
import com.example.ui.theme.StatusDanger

@Composable
fun SparklineChart(
    valuesString: String,
    modifier: Modifier = Modifier,
    isPositive: Boolean = true,
    height: Dp = 40.dp,
    width: Dp = 90.dp
) {
    val prices = valuesString.split(",")
        .mapNotNull { it.trim().toFloatOrNull() }
        .ifEmpty { listOf(100f, 102f, 101f, 104f, 103f, 106f, 108f) }

    val lineColor = if (isPositive) Sprout else StatusDanger
    val gradientColor = if (isPositive) Sprout.copy(alpha = 0.25f) else StatusDanger.copy(alpha = 0.2f)

    Canvas(
        modifier = modifier
            .height(height)
            .width(width)
    ) {
        val w = size.width
        val h = size.height
        val min = prices.minOrNull() ?: 0f
        val max = prices.maxOrNull() ?: 1f
        val range = if (max - min > 0f) max - min else 1f

        val stepX = w / (prices.size - 1).coerceAtLeast(1)

        val points = prices.mapIndexed { index, price ->
            val x = index * stepX
            val normalizedY = (price - min) / range
            val y = h - (normalizedY * (h * 0.75f)) - (h * 0.12f)
            Offset(x, y)
        }

        if (points.size >= 2) {
            // Draw Gradient Area under the sparkline
            val fillPath = Path().apply {
                moveTo(points.first().x, h)
                lineTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val p0 = points[i - 1]
                    val p1 = points[i]
                    val controlPoint1 = Offset((p0.x + p1.x) / 2, p0.y)
                    val controlPoint2 = Offset((p0.x + p1.x) / 2, p1.y)
                    cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                }
                lineTo(points.last().x, h)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(gradientColor, Color.Transparent)
                )
            )

            // Draw Smooth Line
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val p0 = points[i - 1]
                    val p1 = points[i]
                    val controlPoint1 = Offset((p0.x + p1.x) / 2, p0.y)
                    val controlPoint2 = Offset((p0.x + p1.x) / 2, p1.y)
                    cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                }
            }

            drawPath(
                path = strokePath,
                color = lineColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw end dot
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = points.last()
            )
        }
    }
}
