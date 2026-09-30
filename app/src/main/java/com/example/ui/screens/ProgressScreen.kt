package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.UserSettingsEntity
import com.example.data.repository.StreakStats
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.LumaCyan
import com.example.ui.theme.LumaViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProgressScreen(
    stats: StreakStats?,
    userSettings: UserSettingsEntity,
    modifier: Modifier = Modifier
) {
    val currentStreak = stats?.currentStreak ?: 0
    val longestStreak = stats?.longestStreak ?: 0
    val primaryColor = MaterialTheme.colorScheme.primary

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Productivity & Streaks",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Track your consistency and building habits",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Streak Hero Glass Card
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("streak_hero_card"),
                shape = RoundedCornerShape(26.dp),
                backgroundColor = Color(0x22F59E0B)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "STREAK STATUS",
                                color = Color(0xFFFBBF24),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$currentStreak Days",
                            color = TextPrimary,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (currentStreak > 0) "Keep the momentum going! 🔥" else "Complete today's tasks to ignite your streak",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    // Best Streak Badge
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0x33F59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Record",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$longestStreak Days",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4 Key Analytics Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Today",
                    value = "${stats?.completedToday ?: 0}/${stats?.totalToday ?: 0}",
                    icon = Icons.Default.TaskAlt,
                    accentColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "This Week",
                    value = "${stats?.completedThisWeek ?: 0}",
                    icon = Icons.Default.DateRange,
                    accentColor = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "This Month",
                    value = "${stats?.completedThisMonth ?: 0}",
                    icon = Icons.Default.Star,
                    accentColor = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Total Finished",
                    value = "${stats?.totalCompletedAllTime ?: 0}",
                    icon = Icons.Default.Bolt,
                    accentColor = Color(0xFF06B6D4),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 7-Day Activity Chart
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_activity_chart"),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "7-Day Activity",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Daily completion progress over the last week",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val activityDays = stats?.last7DaysActivity ?: emptyList()
                    if (activityDays.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            activityDays.forEach { day ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val percent = day.completionPercentage
                                    val animatedHeight by animateFloatAsState(
                                        targetValue = percent,
                                        animationSpec = tween(durationMillis = 600),
                                        label = "bar_height_${day.date}"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .height(84.dp)
                                            .width(22.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0x18FFFFFF)),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height((84 * animatedHeight).dp.coerceAtLeast(if (day.completedCount > 0) 8.dp else 0.dp))
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            primaryColor,
                                                            LumaCyan
                                                        )
                                                    )
                                                )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = day.dayLabel,
                                        color = if (day.completionPercentage >= 1f) primaryColor else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = if (day.completionPercentage >= 1f) FontWeight.Bold else FontWeight.Normal
                                    )

                                    Text(
                                        text = "${day.completedCount}",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Productivity Insights Section
        item {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Smart Insights",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    InsightRow("Best Day of Week", stats?.bestDayName ?: "Wednesday", "📅")
                    Spacer(modifier = Modifier.height(10.dp))
                    InsightRow("Peak Productive Time", stats?.mostProductiveTime ?: "Morning (🌅)", "⏱️")
                    Spacer(modifier = Modifier.height(10.dp))
                    InsightRow("Streak Rule", if (userSettings.streakRequirement == "ALL_TASKS") "100% of daily tasks" else "At least 1 task daily", "🎯")
                    Spacer(modifier = Modifier.height(10.dp))
                    InsightRow("Daily Completion Target", "${userSettings.dailyGoal} tasks / day", "🏆")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    LiquidGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InsightRow(label: String, value: String, emoji: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, color = TextSecondary, fontSize = 13.sp)
        }
        Text(text = value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
