package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Canopy
import com.example.ui.theme.CanopyDark
import com.example.ui.theme.CanopyLight
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.Husk
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.HuskCard
import com.example.ui.theme.HuskSurface
import com.example.ui.theme.Sprout
import com.example.ui.theme.SproutGlow

// Signature Golden Hour Terraces Glass Card
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    isDarkBg: Boolean = false,
    elevation: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme() || isDarkBg
    val backgroundColor = if (isDark) {
        DarkSurface.copy(alpha = 0.88f)
    } else {
        HuskCard.copy(alpha = 0.92f)
    }

    val borderBrush = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.18f),
                CanopyLight.copy(alpha = 0.4f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.8f),
                HuskBorder,
                HarvestGold.copy(alpha = 0.25f)
            )
        )
    }

    val cardModifier = modifier
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = if (isDark) CanopyDark else HarvestGold.copy(alpha = 0.2f),
            spotColor = if (isDark) Sprout.copy(alpha = 0.25f) else TerracottaLight
        )
        .clip(shape)
        .background(backgroundColor)
        .border(BorderStroke(1.dp, borderBrush), shape)

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = cardModifier,
            color = Color.Transparent,
            shape = shape
        ) {
            content()
        }
    } else {
        Box(
            modifier = cardModifier
        ) {
            content()
        }
    }
}

// Terrace Elevated Card with earthy layered depth
@Composable
fun TerraceCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    accentColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) DarkSurface else HuskSurface

    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier.shadow(
            elevation = 6.dp,
            shape = shape,
            ambientColor = HarvestGold.copy(alpha = 0.15f)
        ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(
            1.dp,
            accentColor?.copy(alpha = 0.5f) ?: if (isDark) DarkBorder else HuskBorder
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (accentColor != null) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .background(accentColor)
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
        }
    }
}

// Glow Orb Effect
@Composable
fun GlowOrb(
    modifier: Modifier = Modifier,
    glowColor: Color = GoldGlow,
    size: Dp = 56.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(size * scale)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        glowColor.copy(alpha = 0.45f),
                        glowColor.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                )
            )
            .blur(16.dp)
    )
}

// Section Header with stylized accent bar
@Composable
fun SectionHeader(
    title: String,
    badgeText: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .size(width = 4.dp, height = 20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(HarvestGold)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (badgeText != null) {
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Sprout.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, Sprout.copy(alpha = 0.4f))
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelMedium,
                    color = Sprout,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
val TerracottaLight = Color(0xFFFBECE6)
