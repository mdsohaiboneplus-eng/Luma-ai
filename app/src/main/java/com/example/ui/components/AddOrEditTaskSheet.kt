package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.model.Priority
import com.example.model.RecurrenceType
import com.example.model.Subtask
import com.example.model.TaskCategory
import com.example.model.TaskWithStatus
import com.example.ui.theme.LiquidDarkSurface
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddOrEditTaskSheet(
    initialDate: String,
    taskToEdit: TaskWithStatus?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        description: String,
        date: String,
        time: String?,
        priority: Priority,
        recurrence: RecurrenceType,
        customDays: String,
        reminderEnabled: Boolean,
        reminderTime: String?,
        category: String,
        subtasks: List<Subtask>
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var selectedDate by remember { mutableStateOf(taskToEdit?.date ?: initialDate) }
    var selectedTime by remember { mutableStateOf(taskToEdit?.time ?: "") }
    var selectedPriority by remember { mutableStateOf(taskToEdit?.priority ?: Priority.MEDIUM) }
    var selectedRecurrence by remember { mutableStateOf(taskToEdit?.recurrence ?: RecurrenceType.NONE) }
    var customDays by remember { mutableStateOf(taskToEdit?.customDays ?: "") }
    var reminderEnabled by remember { mutableStateOf(taskToEdit?.reminderEnabled ?: false) }
    var reminderTime by remember { mutableStateOf(taskToEdit?.reminderTime ?: "09:00") }
    var selectedCategory by remember { mutableStateOf(taskToEdit?.category ?: "Personal") }
    var subtasks by remember { mutableStateOf(taskToEdit?.subtasks ?: emptyList()) }
    var newSubtaskText by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LiquidDarkSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 38.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color(0x40FFFFFF))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (taskToEdit == null) "New Task" else "Edit Task",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task Title Input
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) titleError = false
                },
                placeholder = { Text("What do you want to accomplish?", color = TextMuted) },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Title is required", color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = LiquidGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_title_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Notes or details (optional)", color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = LiquidGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_desc_input")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Date Quick Selectors (Today, Tomorrow, Later)
            Text("Date", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val todayStr = DateUtils.getTodayString()
                val tomorrowStr = DateUtils.addDays(todayStr, 1)

                listOf(
                    "Today" to todayStr,
                    "Tomorrow" to tomorrowStr,
                    selectedDate to selectedDate
                ).distinctBy { it.second }.forEach { (label, dateVal) ->
                    val isSelected = selectedDate == dateVal
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color(0x18FFFFFF)
                            )
                            .clickable { selectedDate = dateVal }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (dateVal == todayStr) "Today" else if (dateVal == tomorrowStr) "Tomorrow" else DateUtils.formatShortDate(dateVal),
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Time Selector
            Text("Time", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val timeOptions = listOf(
                    "" to "Anytime",
                    "07:30" to "7:30 AM",
                    "09:00" to "9:00 AM",
                    "13:00" to "1:00 PM",
                    "15:00" to "3:00 PM",
                    "19:00" to "7:00 PM",
                    "21:00" to "9:00 PM"
                )

                timeOptions.forEach { (timeVal, timeLabel) ->
                    val isSelected = selectedTime == timeVal
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color(0x18FFFFFF)
                            )
                            .clickable { selectedTime = timeVal }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = timeLabel,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Priority Selector
            Text("Priority", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.entries.forEach { p ->
                    val isSelected = selectedPriority == p
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) p.color.copy(alpha = 0.25f)
                                else Color(0x18FFFFFF)
                            )
                            .border(
                                1.dp,
                                if (isSelected) p.color else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedPriority = p }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p.displayName,
                            color = if (isSelected) p.color else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Recurrence / Habit Repeat Selector
            Text("Repeat", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecurrenceType.entries.forEach { rec ->
                    val isSelected = selectedRecurrence == rec
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color(0x18FFFFFF)
                            )
                            .clickable { selectedRecurrence = rec }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = rec.displayName,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            // Custom recurrence day picker if CUSTOM is selected
            if (selectedRecurrence == RecurrenceType.CUSTOM) {
                Spacer(modifier = Modifier.height(10.dp))
                val daysOfWeek = listOf(
                    "2" to "M",
                    "3" to "T",
                    "4" to "W",
                    "5" to "T",
                    "6" to "F",
                    "7" to "S",
                    "1" to "S"
                )
                val activeDaysList = customDays.split(",").map { it.trim() }.toMutableSet()

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    daysOfWeek.forEach { (calDay, label) ->
                        val isActive = activeDaysList.contains(calDay)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) MaterialTheme.colorScheme.primary
                                    else Color(0x1AFFFFFF)
                                )
                                .clickable {
                                    if (isActive) activeDaysList.remove(calDay)
                                    else activeDaysList.add(calDay)
                                    customDays = activeDaysList.joinToString(",")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isActive) Color.White else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category Selector
            Text("Category", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskCategory.ALL.forEach { cat ->
                    val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color(0x18FFFFFF)
                            )
                            .clickable { selectedCategory = cat.name }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "${cat.iconEmoji} ${cat.name}",
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Reminder Toggle & Setting
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0x14FFFFFF)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Set Reminder", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Receive notification on time", color = TextMuted, fontSize = 12.sp)
                        }
                    }

                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Subtasks Builder
            Text("Subtasks", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            subtasks.forEachIndexed { index, sub ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (sub.isCompleted) MaterialTheme.colorScheme.primary
                                else Color(0x18FFFFFF)
                            )
                            .border(1.dp, Color(0x66FFFFFF), CircleShape)
                            .clickable {
                                subtasks = subtasks.toMutableList().also { list ->
                                    list[index] = sub.copy(isCompleted = !sub.isCompleted)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (sub.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = sub.title,
                        color = if (sub.isCompleted) TextMuted else TextPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            subtasks = subtasks.toMutableList().also { it.removeAt(index) }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove subtask",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Add new subtask input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newSubtaskText,
                    onValueChange = { newSubtaskText = it },
                    placeholder = { Text("Add subtask...", color = TextMuted, fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = LiquidGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (newSubtaskText.isNotBlank()) {
                            subtasks = subtasks + Subtask(
                                id = UUID.randomUUID().toString(),
                                title = newSubtaskText.trim(),
                                isCompleted = false
                            )
                            newSubtaskText = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add subtask",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Save Action Button
            LiquidGlassButton(
                text = if (taskToEdit == null) "Create Task ✨" else "Save Changes",
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        onSave(
                            taskToEdit?.id ?: 0L,
                            title,
                            description,
                            selectedDate,
                            selectedTime,
                            selectedPriority,
                            selectedRecurrence,
                            customDays,
                            reminderEnabled,
                            reminderTime,
                            selectedCategory,
                            subtasks
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_task_button")
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
