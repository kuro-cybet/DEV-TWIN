package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SproutGlow,
    onPrimary = CanopyDark,
    primaryContainer = CanopyContainer,
    onPrimaryContainer = SproutLight,
    secondary = HarvestGold,
    onSecondary = CanopyDark,
    secondaryContainer = CanopyLight,
    onSecondaryContainer = GoldLight,
    tertiary = Terracotta,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = CanopyLight
)

private val LightColorScheme = lightColorScheme(
    primary = Canopy,
    onPrimary = Husk,
    primaryContainer = SproutLight,
    onPrimaryContainer = Canopy,
    secondary = HarvestGold,
    onSecondary = Canopy,
    secondaryContainer = GoldLight,
    onSecondaryContainer = GoldDark,
    tertiary = Terracotta,
    onTertiary = Color.White,
    background = Husk,
    onBackground = Canopy,
    surface = HuskSurface,
    onSurface = Canopy,
    surfaceVariant = HuskCard,
    onSurfaceVariant = CanopyLight,
    outline = HuskBorder,
    outlineVariant = HuskBorder.copy(alpha = 0.6f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep brand "Golden Hour Terraces" styling
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
