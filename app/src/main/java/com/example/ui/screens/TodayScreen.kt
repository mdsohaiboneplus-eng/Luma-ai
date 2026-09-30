package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.UserSettingsEntity
import com.example.data.repository.StreakStats
import com.example.model.TaskWithStatus
import com.example.model.TimeOfDayGroup
import com.example.ui.components.LiquidGlassButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.TaskItemRow
import com.example.ui.components.TodayProgressCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@Composable
fun TodayScreen(
    tasks: List<TaskWithStatus>,
    userSettings: UserSettingsEntity,
    streakStats: StreakStats?,
    onToggleTask: (Long, Boolean) -> Unit,
    onEditTask: (TaskWithStatus) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onDuplicateTask: (Long) -> Unit,
    onAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = tasks.count { it.isCompletedForDate }
    val totalCount = tasks.size
    val greeting = DateUtils.getGreeting(userSettings.userName)
    val dateHeader = DateUtils.formatHeaderDate(DateUtils.getTodayString())

    // Group tasks by Time of Day
    val morningTasks = tasks.filter { it.timeOfDay == TimeOfDayGroup.MORNING }
    val afternoonTasks = tasks.filter { it.timeOfDay == TimeOfDayGroup.AFTERNOON }
    val eveningTasks = tasks.filter { it.timeOfDay == TimeOfDayGroup.EVENING }
    val anytimeTasks = tasks.filter { it.timeOfDay == TimeOfDayGroup.ANYTIME }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // User Greeting & Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = greeting,
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dateHeader,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    // User Avatar Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userSettings.userAvatar,
                            fontSize = 22.sp
                        )
                    }
                }
            }

            // Progress Card
            item {
                TodayProgressCard(
                    completedCount = completedCount,
                    totalCount = totalCount,
                    currentStreak = streakStats?.currentStreak ?: 0
                )
            }

            // Today's Tasks Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Tasks",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$completedCount/$totalCount done",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }

            // Empty State
            if (tasks.isEmpty()) {
                item {
                    LiquidGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No tasks for today",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your day is clear. Enjoy the extra space.",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            LiquidGlassButton(
                                text = "+ Add your first task",
                                onClick = onAddTask,
                                modifier = Modifier.testTag("empty_add_task_button")
                            )
                        }
                    }
                }
            } else {
                // Group: Morning
                if (morningTasks.isNotEmpty()) {
                    item {
                        TimeGroupHeader("🌅 Morning")
                    }
                    items(morningTasks.size, key = { "morning_${morningTasks[it].id}" }) { idx ->
                        val task = morningTasks[idx]
                        TaskItemRow(
                            task = task,
                            onToggleComplete = { onToggleTask(task.id, it) },
                            onEdit = { onEditTask(task) },
                            onDelete = { onDeleteTask(task.id) },
                            onDuplicate = { onDuplicateTask(task.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Group: Afternoon
                if (afternoonTasks.isNotEmpty()) {
                    item {
                        TimeGroupHeader("☀️ Afternoon")
                    }
                    items(afternoonTasks.size, key = { "afternoon_${afternoonTasks[it].id}" }) { idx ->
                        val task = afternoonTasks[idx]
                        TaskItemRow(
                            task = task,
                            onToggleComplete = { onToggleTask(task.id, it) },
                            onEdit = { onEditTask(task) },
                            onDelete = { onDeleteTask(task.id) },
                            onDuplicate = { onDuplicateTask(task.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Group: Evening
                if (eveningTasks.isNotEmpty()) {
                    item {
                        TimeGroupHeader("🌙 Evening")
                    }
                    items(eveningTasks.size, key = { "evening_${eveningTasks[it].id}" }) { idx ->
                        val task = eveningTasks[idx]
                        TaskItemRow(
                            task = task,
                            onToggleComplete = { onToggleTask(task.id, it) },
                            onEdit = { onEditTask(task) },
                            onDelete = { onDeleteTask(task.id) },
                            onDuplicate = { onDuplicateTask(task.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Group: Anytime
                if (anytimeTasks.isNotEmpty()) {
                    item {
                        TimeGroupHeader("⚡ Anytime")
                    }
                    items(anytimeTasks.size, key = { "anytime_${anytimeTasks[it].id}" }) { idx ->
                        val task = anytimeTasks[idx]
                        TaskItemRow(
                            task = task,
                            onToggleComplete = { onToggleTask(task.id, it) },
                            onEdit = { onEditTask(task) },
                            onDelete = { onDeleteTask(task.id) },
                            onDuplicate = { onDuplicateTask(task.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Bottom spacing for navigation bar + FAB
            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddTask,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp)
                .testTag("today_fab_add_task")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun TimeGroupHeader(title: String) {
    Text(
        text = title,
        color = TextSecondary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}
