package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun getLiquidDarkColorScheme(accent: Color = LumaViolet) = darkColorScheme(
    primary = accent,
    onPrimary = Color.White,
    primaryContainer = accent.copy(alpha = 0.25f),
    onPrimaryContainer = Color.White,
    secondary = LumaCyan,
    onSecondary = Color.White,
    tertiary = LumaEmerald,
    background = LiquidDarkBackground,
    onBackground = TextPrimary,
    surface = LiquidDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF161E2E),
    onSurfaceVariant = TextSecondary,
    outline = LiquidGlassBorder
)

fun getLiquidMidnightColorScheme(accent: Color = LumaViolet) = darkColorScheme(
    primary = accent,
    onPrimary = Color.White,
    primaryContainer = accent.copy(alpha = 0.25f),
    onPrimaryContainer = Color.White,
    secondary = LumaCyan,
    onSecondary = Color.White,
    tertiary = LumaEmerald,
    background = LiquidMidnightBackground,
    onBackground = TextPrimary,
    surface = Color(0xFF0C1018),
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF131924),
    onSurfaceVariant = TextSecondary,
    outline = LiquidGlassBorder
)

fun getLiquidLightColorScheme(accent: Color = LumaVioletDark) = lightColorScheme(
    primary = accent,
    onPrimary = Color.White,
    primaryContainer = accent.copy(alpha = 0.15f),
    onPrimaryContainer = accent,
    secondary = LumaCyan,
    onSecondary = Color.White,
    tertiary = LumaEmerald,
    background = LiquidLightBackground,
    onBackground = TextLightPrimary,
    surface = LiquidLightSurface,
    onSurface = TextLightPrimary,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = TextLightSecondary,
    outline = LiquidLightBorder
)

@Composable
fun LumaTaskTheme(
    themeMode: String = "LIQUID_DARK",
    accentName: String = "VIOLET",
    content: @Composable () -> Unit
) {
    val accent = when (accentName.uppercase()) {
        "CYAN" -> LumaCyan
        "EMERALD" -> LumaEmerald
        "AMBER" -> LumaAmber
        else -> LumaViolet
    }

    val colorScheme = when (themeMode) {
        "DEEP_MIDNIGHT" -> getLiquidMidnightColorScheme(accent)
        "GLASS_LIGHT" -> getLiquidLightColorScheme(accent)
        else -> getLiquidDarkColorScheme(accent)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
