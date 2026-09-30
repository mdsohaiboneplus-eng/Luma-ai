package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

data class NavItem(
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val testTag: String
)

@Composable
fun LiquidBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem("Today", Icons.Default.Today, Icons.Outlined.Today, "tab_today"),
        NavItem("Tasks", Icons.Default.CheckCircle, Icons.Outlined.CheckCircle, "tab_tasks"),
        NavItem("Calendar", Icons.Default.CalendarMonth, Icons.Outlined.CalendarMonth, "tab_calendar"),
        NavItem("Progress", Icons.Default.Equalizer, Icons.Outlined.Equalizer, "tab_progress"),
        NavItem("Settings", Icons.Default.Settings, Icons.Outlined.Settings, "tab_settings")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x28FFFFFF),
                            Color(0x18FFFFFF)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x40FFFFFF),
                            Color(0x14FFFFFF)
                        )
                    ),
                    RoundedCornerShape(26.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    val interactionSource = remember { MutableInteractionSource() }

                    val scaleAnim by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.7f),
                        label = "nav_scale"
                    )

                    val activeColor = MaterialTheme.colorScheme.primary

                    val tintColor by animateColorAsState(
                        targetValue = if (isSelected) activeColor else TextMuted,
                        label = "nav_color"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .scale(scaleAnim)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) activeColor.copy(alpha = 0.16f)
                                else Color.Transparent
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onTabSelected(index) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag(item.testTag)
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                            contentDescription = item.label,
                            tint = tintColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            color = if (isSelected) TextPrimary else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
