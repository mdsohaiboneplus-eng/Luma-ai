package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LumaCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TodayProgressCard(
    completedCount: Int,
    totalCount: Int,
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    val targetProgress = if (totalCount > 0) (completedCount.toFloat() / totalCount).coerceIn(0f, 1f) else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progress_anim"
    )

    val percentage = (animatedProgress * 100).toInt()
    val primaryColor = MaterialTheme.colorScheme.primary

    LiquidGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("today_progress_card"),
        shape = RoundedCornerShape(26.dp),
        backgroundColor = Color(0x1AFFFFFF),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TODAY'S PROGRESS",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    if (currentStreak > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x22F59E0B))
                                .border(1.dp, Color(0x44F59E0B), RoundedCornerShape(10.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🔥 $currentStreak",
                                color = Color(0xFFFBBF24),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$percentage%",
                    color = TextPrimary,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$completedCount of $totalCount tasks completed",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )

                if (totalCount > 0 && completedCount >= totalCount) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🎉 All tasks finished! Streak secured.",
                        color = Color(0xFF34D399),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Circular progress indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(88.dp)
            ) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    val strokeWidth = 8.dp.toPx()

                    // Background track
                    drawCircle(
                        color = Color(0x20FFFFFF),
                        style = Stroke(width = strokeWidth)
                    )

                    // Foreground animated arc
                    val sweepAngle = animatedProgress * 360f
                    if (sweepAngle > 0f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    primaryColor,
                                    LumaCyan,
                                    primaryColor
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }

                // Inner badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x1EFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (percentage == 100) "✨" else "${percentage}%",
                        color = TextPrimary,
                        fontSize = if (percentage == 100) 18.sp else 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
