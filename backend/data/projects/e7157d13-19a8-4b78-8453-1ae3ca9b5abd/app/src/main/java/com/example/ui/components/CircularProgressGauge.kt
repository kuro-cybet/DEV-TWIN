package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.Sprout
import com.example.ui.theme.SproutGlow
import com.example.ui.theme.StatusWarning

@Composable
fun CircularProgressGauge(
    percentage: Int,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    labelText: String? = null,
    gradientColors: List<Color> = listOf(HarvestGold, SproutGlow)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 1200),
        label = "progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            // Track background circle
            drawArc(
                color = HuskBorder.copy(alpha = 0.4f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        gradientColors.first(),
                        gradientColors.last(),
                        gradientColors.first()
                    )
                ),
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (labelText != null) {
                Text(
                    text = labelText,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// NPK Status Ring / Metric Indicator
@Composable
fun NutrientRadialMetric(
    nutrientName: String,
    level: String, // "Low", "Optimal", "High"
    modifier: Modifier = Modifier
) {
    val (color, progress) = when (level.lowercase()) {
        "low" -> StatusWarning to 0.35f
        "high" -> HarvestGold to 0.95f
        else -> Sprout to 0.70f
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(54.dp)
        ) {
            Canvas(modifier = Modifier.size(54.dp)) {
                drawArc(
                    color = HuskBorder.copy(alpha = 0.35f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(
                text = nutrientName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = level,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
