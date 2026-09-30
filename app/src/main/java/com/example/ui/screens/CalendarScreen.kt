package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskWithStatus
import com.example.ui.components.LiquidGlassButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.TaskItemRow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    dayTasks: List<TaskWithStatus>,
    onToggleTask: (Long, Boolean) -> Unit,
    onEditTask: (TaskWithStatus) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onDuplicateTask: (Long) -> Unit,
    onAddTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cal = remember {
        Calendar.getInstance().apply {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            try {
                time = sdf.parse(selectedDate) ?: java.util.Date()
            } catch (e: Exception) {
                // Keep default
            }
        }
    }

    var displayedYear by remember { mutableStateOf(cal.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableStateOf(cal.get(Calendar.MONTH)) }

    val monthYearText = remember(displayedYear, displayedMonth) {
        val tempCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        SimpleDateFormat("MMMM yyyy", Locale.US).format(tempCal.time)
    }

    val completedCount = dayTasks.count { it.isCompletedForDate }
    val totalCount = dayTasks.size
    val completionPercent = if (totalCount > 0) (completedCount * 100) / totalCount else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Calendar",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }

        // Calendar Card
        item {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Month navigation header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (displayedMonth == 0) {
                                    displayedMonth = 11
                                    displayedYear--
                                } else {
                                    displayedMonth--
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Month",
                                tint = TextPrimary
                            )
                        }

                        Text(
                            text = monthYearText,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = {
                                if (displayedMonth == 11) {
                                    displayedMonth = 0
                                    displayedYear++
                                } else {
                                    displayedMonth++
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Month",
                                tint = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Days of week row
                    val weekdays = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekdays.forEach { dayName ->
                            Text(
                                text = dayName,
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Calendar Month Grid
                    val gridDays = remember(displayedYear, displayedMonth) {
                        buildMonthDays(displayedYear, displayedMonth)
                    }

                    val rows = gridDays.chunked(7)
                    rows.forEach { week ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            week.forEach { dayInfo ->
                                if (dayInfo == null) {
                                    Box(modifier = Modifier.size(38.dp))
                                } else {
                                    val isSelected = dayInfo.isoDate == selectedDate
                                    val isToday = dayInfo.isoDate == DateUtils.getTodayString()

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else if (isToday) Color(0x24FFFFFF)
                                                else Color.Transparent
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                                else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable { onSelectDate(dayInfo.isoDate) }
                                            .testTag("cal_day_${dayInfo.isoDate}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "${dayInfo.dayNumber}",
                                                color = if (isSelected) Color.White else TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Date Details
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = DateUtils.formatHeaderDate(selectedDate),
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (totalCount > 0) "$completedCount of $totalCount completed ($completionPercent%)"
                        else "No tasks scheduled for this day",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                LiquidGlassButton(
                    text = "+ Add",
                    onClick = { onAddTask(selectedDate) }
                )
            }
        }

        // Tasks for selected day
        if (dayTasks.isEmpty()) {
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tasks for this day. Tap '+ Add' above to plan.",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(dayTasks.size, key = { dayTasks[it].id }) { idx ->
                val task = dayTasks[idx]
                TaskItemRow(
                    task = task,
                    onToggleComplete = { onToggleTask(task.id, it) },
                    onEdit = { onEditTask(task) },
                    onDelete = { onDeleteTask(task.id) },
                    onDuplicate = { onDuplicateTask(task.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

data class CalendarDayInfo(
    val dayNumber: Int,
    val isoDate: String
)

private fun buildMonthDays(year: Int, month: Int): List<CalendarDayInfo?> {
    val list = mutableListOf<CalendarDayInfo?>()
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    // 1-based day of week (Sunday=1, Monday=2)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // Sunday=0
    for (i in 0 until firstDayOfWeek) {
        list.add(null)
    }

    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    for (day in 1..maxDays) {
        cal.set(Calendar.DAY_OF_MONTH, day)
        list.add(CalendarDayInfo(day, sdf.format(cal.time)))
    }

    return list
}
