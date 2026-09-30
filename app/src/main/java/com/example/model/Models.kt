package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class Priority(val displayName: String, val color: Color) {
    LOW("Low", PriorityLow),
    MEDIUM("Medium", PriorityMedium),
    HIGH("High", PriorityHigh);

    companion object {
        fun fromString(value: String): Priority {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}

enum class RecurrenceType(val displayName: String) {
    NONE("Does not repeat"),
    DAILY("Every day"),
    WEEKDAYS("Weekdays (Mon-Fri)"),
    WEEKENDS("Weekends (Sat-Sun)"),
    WEEKLY("Every week"),
    MONTHLY("Every month"),
    CUSTOM("Custom");

    companion object {
        fun fromString(value: String): RecurrenceType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: NONE
        }
    }
}

enum class TimeOfDayGroup(val displayName: String, val iconEmoji: String) {
    MORNING("Morning", "🌅"),
    AFTERNOON("Afternoon", "☀️"),
    EVENING("Evening", "🌙"),
    ANYTIME("Anytime", "⚡");

    companion object {
        fun fromTime(timeStr: String?): TimeOfDayGroup {
            if (timeStr.isNullOrBlank()) return ANYTIME
            return try {
                val parts = timeStr.split(":")
                val hour = parts[0].toIntOrNull() ?: return ANYTIME
                when {
                    hour < 12 -> MORNING
                    hour in 12..16 -> AFTERNOON
                    else -> EVENING
                }
            } catch (e: Exception) {
                ANYTIME
            }
        }
    }
}

data class Subtask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)

data class TaskCategory(
    val name: String,
    val iconEmoji: String,
    val colorHex: String
) {
    companion object {
        val ALL = listOf(
            TaskCategory("Personal", "👤", "#8B5CF6"),
            TaskCategory("Work", "💼", "#3B82F6"),
            TaskCategory("Health", "🏃", "#10B981"),
            TaskCategory("Study", "📚", "#F59E0B"),
            TaskCategory("Faith", "🕊️", "#06B6D4"),
            TaskCategory("Focus", "🎯", "#EC4899")
        )

        fun find(name: String): TaskCategory {
            return ALL.firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: TaskCategory(name, "📌", "#8B5CF6")
        }
    }
}

data class TaskWithStatus(
    val id: Long,
    val title: String,
    val description: String,
    val date: String, // Base scheduled date YYYY-MM-DD
    val targetDate: String, // The occurrence date being viewed YYYY-MM-DD
    val time: String?,
    val priority: Priority,
    val recurrence: RecurrenceType,
    val customDays: String,
    val reminderEnabled: Boolean,
    val reminderTime: String?,
    val category: String,
    val subtasks: List<Subtask>,
    val isCompletedForDate: Boolean,
    val completedAt: Long?,
    val isArchived: Boolean = false,
    val createdAt: Long
) {
    val timeOfDay: TimeOfDayGroup get() = TimeOfDayGroup.fromTime(time)
}

object RecurrenceEvaluator {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun isTaskScheduledOnDate(
        recurrence: RecurrenceType,
        customDays: String,
        baseDateStr: String,
        targetDateStr: String
    ): Boolean {
        // A task cannot appear before its base creation date
        if (targetDateStr < baseDateStr) return false

        if (recurrence == RecurrenceType.NONE) {
            return targetDateStr == baseDateStr
        }

        return try {
            val targetCal = Calendar.getInstance().apply {
                time = dateFormat.parse(targetDateStr) ?: return false
            }
            val baseCal = Calendar.getInstance().apply {
                time = dateFormat.parse(baseDateStr) ?: return false
            }

            // Calendar.DAY_OF_WEEK: Sunday=1, Monday=2, ..., Saturday=7
            val dayOfWeek = targetCal.get(Calendar.DAY_OF_WEEK)

            when (recurrence) {
                RecurrenceType.DAILY -> true
                RecurrenceType.WEEKDAYS -> dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY
                RecurrenceType.WEEKENDS -> dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
                RecurrenceType.WEEKLY -> {
                    // Same day of week as base date
                    dayOfWeek == baseCal.get(Calendar.DAY_OF_WEEK)
                }
                RecurrenceType.MONTHLY -> {
                    // Same day of month as base date
                    targetCal.get(Calendar.DAY_OF_MONTH) == baseCal.get(Calendar.DAY_OF_MONTH)
                }
                RecurrenceType.CUSTOM -> {
                    // customDays format e.g. "2,3,4" mapping Calendar.DAY_OF_WEEK (1..7)
                    if (customDays.isBlank()) true
                    else {
                        val activeDays = customDays.split(",").mapNotNull { it.trim().toIntOrNull() }
                        activeDays.contains(dayOfWeek)
                    }
                }
                RecurrenceType.NONE -> targetDateStr == baseDateStr
            }
        } catch (e: Exception) {
            targetDateStr == baseDateStr
        }
    }
}
