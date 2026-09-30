package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.TextPrimary

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = Color(0x18FFFFFF),
    borderColor: Color = LiquidGlassBorder,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation, shape, clip = false)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = (backgroundColor.alpha * 0.5f).coerceIn(0f, 1f))
                    )
                )
            )
            .border(
                BorderStroke(
                    borderWidth,
                    Brush.linearGradient(
                        colors = listOf(
                            borderColor,
                            borderColor.copy(alpha = 0.08f)
                        )
                    )
                ),
                shape
            )
    ) {
        content()
    }
}

@Composable
fun GlowBadge(
    text: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.16f))
            .border(
                1.dp,
                accentColor.copy(alpha = 0.35f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = accentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LiquidGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    isSecondary: Boolean = false,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    val bgBrush = if (isSecondary) {
        Brush.linearGradient(
            colors = listOf(
                Color(0x22FFFFFF),
                Color(0x0EFFFFFF)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                primaryColor,
                primaryColor.copy(alpha = 0.85f)
            )
        )
    }

    val borderStroke = if (isSecondary) {
        BorderStroke(1.dp, Color(0x33FFFFFF))
    } else {
        BorderStroke(1.dp, primaryColor.copy(alpha = 0.9f))
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = borderStroke,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgBrush)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                if (icon != null) {
                    icon()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                }
                Text(
                    text = text,
                    color = if (isSecondary) TextPrimary else Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
