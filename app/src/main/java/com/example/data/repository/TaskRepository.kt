package com.example.data.repository

import android.content.Context
import com.example.data.db.LumaDatabase
import com.example.data.db.LumaTaskDao
import com.example.data.entity.TaskCompletionEntity
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserSettingsEntity
import com.example.model.Priority
import com.example.model.RecurrenceEvaluator
import com.example.model.RecurrenceType
import com.example.model.TaskWithStatus
import com.example.model.TimeOfDayGroup
import com.example.reminder.ReminderScheduler
import com.example.util.DateUtils
import com.example.util.JsonUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DayActivity(
    val date: String,
    val dayLabel: String,
    val completedCount: Int,
    val totalCount: Int
) {
    val completionPercentage: Float
        get() = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
}

data class StreakStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val completedToday: Int,
    val totalToday: Int,
    val completedThisWeek: Int,
    val completedThisMonth: Int,
    val totalCompletedAllTime: Int,
    val bestDayName: String,
    val mostProductiveTime: String,
    val last7DaysActivity: List<DayActivity>
)

class TaskRepository(
    private val context: Context,
    private val dao: LumaTaskDao
) {
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val allCompletions: Flow<List<TaskCompletionEntity>> = dao.getAllCompletions()
    val userSettings: Flow<UserSettingsEntity?> = dao.getUserSettings()

    fun getTasksForDate(targetDate: String): Flow<List<TaskWithStatus>> {
        return combine(dao.getAllTasks(), dao.getCompletionsForDate(targetDate)) { tasks, completions ->
            val compMap = completions.associateBy { it.taskId }
            tasks.filter { task ->
                RecurrenceEvaluator.isTaskScheduledOnDate(
                    recurrence = RecurrenceType.fromString(task.recurrence),
                    customDays = task.customDays,
                    baseDateStr = task.date,
                    targetDateStr = targetDate
                )
            }.map { task ->
                val comp = compMap[task.id]
                TaskWithStatus(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    date = task.date,
                    targetDate = targetDate,
                    time = task.time,
                    priority = Priority.fromString(task.priority),
                    recurrence = RecurrenceType.fromString(task.recurrence),
                    customDays = task.customDays,
                    reminderEnabled = task.reminderEnabled,
                    reminderTime = task.reminderTime,
                    category = task.category,
                    subtasks = JsonUtils.parseSubtasks(task.subtasksJson),
                    isCompletedForDate = comp?.isCompleted == true,
                    completedAt = comp?.completedAt,
                    isArchived = task.isArchived,
                    createdAt = task.createdAt
                )
            }
        }
    }

    suspend fun insertTask(task: TaskEntity): Long {
        val id = dao.insertTask(task)
        val created = task.copy(id = id)
        ReminderScheduler.scheduleTaskReminder(context, created)
        return id
    }

    suspend fun updateTask(task: TaskEntity) {
        dao.updateTask(task)
        ReminderScheduler.scheduleTaskReminder(context, task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        dao.deleteTask(task)
        dao.deleteAllCompletionsForTask(task.id)
        ReminderScheduler.cancelTaskReminder(context, task.id)
    }

    suspend fun toggleTaskCompletion(taskId: Long, targetDate: String, isCompleted: Boolean) {
        val existing = dao.getCompletion(taskId, targetDate)
        val updated = existing?.copy(
            isCompleted = isCompleted,
            completedAt = System.currentTimeMillis()
        ) ?: TaskCompletionEntity(
            taskId = taskId,
            date = targetDate,
            isCompleted = isCompleted,
            completedAt = System.currentTimeMillis()
        )
        dao.insertOrReplaceCompletion(updated)
    }

    suspend fun duplicateTask(taskId: Long): Long {
        val original = dao.getTaskById(taskId) ?: return -1L
        val copy = original.copy(
            id = 0,
            title = "${original.title} (Copy)",
            createdAt = System.currentTimeMillis()
        )
        return insertTask(copy)
    }

    suspend fun updateUserSettings(settings: UserSettingsEntity) {
        dao.insertOrUpdateSettings(settings)
    }

    suspend fun calculateStats(streakRequirement: String, dailyGoal: Int): StreakStats {
        val allTasks = dao.getAllTasksList()
        val allCompletions = dao.getAllCompletionsList()
        val todayStr = DateUtils.getTodayString()

        val compByDateAndTask = allCompletions.groupBy { it.date }
            .mapValues { (_, list) -> list.associateBy { it.taskId } }

        // Last 7 days activity
        val past7Days = DateUtils.getPastDays(7)
        val last7DaysActivity = past7Days.map { dateStr ->
            val tasksOnDay = allTasks.filter { task ->
                RecurrenceEvaluator.isTaskScheduledOnDate(
                    RecurrenceType.fromString(task.recurrence),
                    task.customDays,
                    task.date,
                    dateStr
                )
            }
            val dateCompletions = compByDateAndTask[dateStr] ?: emptyMap()
            val completedCount = tasksOnDay.count { dateCompletions[it.id]?.isCompleted == true }
            DayActivity(
                date = dateStr,
                dayLabel = DateUtils.getDayOfWeekLabel(dateStr),
                completedCount = completedCount,
                totalCount = tasksOnDay.size
            )
        }

        // Streak calculation
        var currentStreak = 0
        var longestStreak = 0
        var runningStreak = 0

        // Check back up to 90 days for streaks
        val past90Days = DateUtils.getPastDays(90).reversed() // today down to 90 days ago
        var checkStreakContinues = true

        for ((index, dateStr) in past90Days.withIndex()) {
            val tasksOnDay = allTasks.filter { task ->
                RecurrenceEvaluator.isTaskScheduledOnDate(
                    RecurrenceType.fromString(task.recurrence),
                    task.customDays,
                    task.date,
                    dateStr
                )
            }
            val dateCompletions = compByDateAndTask[dateStr] ?: emptyMap()
            val completedCount = tasksOnDay.count { dateCompletions[it.id]?.isCompleted == true }
            val totalCount = tasksOnDay.size

            val isDaySuccessful = when (streakRequirement) {
                "AT_LEAST_ONE" -> completedCount >= 1
                else -> totalCount > 0 && completedCount >= totalCount
            }

            if (isDaySuccessful) {
                runningStreak++
                if (runningStreak > longestStreak) longestStreak = runningStreak
                if (checkStreakContinues) currentStreak = runningStreak
            } else {
                // If today is not yet successful, don't break the streak if yesterday was successful
                if (index == 0) {
                    // Today in progress, streak continues if yesterday was successful
                } else {
                    checkStreakContinues = false
                }
                runningStreak = 0
            }
        }

        // Today's counts
        val todayActivity = last7DaysActivity.lastOrNull()
        val completedToday = todayActivity?.completedCount ?: 0
        val totalToday = todayActivity?.totalCount ?: 0

        // Week and Month counts
        val past7Completed = last7DaysActivity.sumOf { it.completedCount }
        val past30Days = DateUtils.getPastDays(30)
        var completedThisMonth = 0
        for (dateStr in past30Days) {
            val tasksOnDay = allTasks.filter { task ->
                RecurrenceEvaluator.isTaskScheduledOnDate(
                    RecurrenceType.fromString(task.recurrence),
                    task.customDays,
                    task.date,
                    dateStr
                )
            }
            val dateCompletions = compByDateAndTask[dateStr] ?: emptyMap()
            completedThisMonth += tasksOnDay.count { dateCompletions[it.id]?.isCompleted == true }
        }

        val totalCompletedAllTime = allCompletions.count { it.isCompleted }

        // Most productive time of day
        val completedTasks = allTasks.filter { task ->
            allCompletions.any { it.taskId == task.id && it.isCompleted }
        }
        val morningCount = completedTasks.count { TimeOfDayGroup.fromTime(it.time) == TimeOfDayGroup.MORNING }
        val afternoonCount = completedTasks.count { TimeOfDayGroup.fromTime(it.time) == TimeOfDayGroup.AFTERNOON }
        val eveningCount = completedTasks.count { TimeOfDayGroup.fromTime(it.time) == TimeOfDayGroup.EVENING }
        val mostProductiveTime = when {
            morningCount >= afternoonCount && morningCount >= eveningCount -> "Morning (🌅)"
            afternoonCount >= eveningCount -> "Afternoon (☀️)"
            else -> "Evening (🌙)"
        }

        // Best Day Name
        val dayCounts = mutableMapOf<String, Int>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (comp in allCompletions.filter { it.isCompleted }) {
            try {
                val cal = Calendar.getInstance().apply { time = sdf.parse(comp.date) ?: return@apply }
                val dayName = SimpleDateFormat("EEEE", Locale.US).format(cal.time)
                dayCounts[dayName] = (dayCounts[dayName] ?: 0) + 1
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }
        val bestDayName = dayCounts.maxByOrNull { it.value }?.key ?: "Wednesday"

        return StreakStats(
            currentStreak = currentStreak,
            longestStreak = longestStreak.coerceAtLeast(currentStreak),
            completedToday = completedToday,
            totalToday = totalToday,
            completedThisWeek = past7Completed,
            completedThisMonth = completedThisMonth,
            totalCompletedAllTime = totalCompletedAllTime,
            bestDayName = bestDayName,
            mostProductiveTime = mostProductiveTime,
            last7DaysActivity = last7DaysActivity
        )
    }

    suspend fun exportJson(): String {
        val tasks = dao.getAllTasksList()
        val completions = dao.getAllCompletionsList()
        val settings = dao.getUserSettingsDirect()
        return JsonUtils.exportToJson(tasks, completions, settings)
    }

    suspend fun importJson(json: String, merge: Boolean = false): Result<Unit> {
        return JsonUtils.importFromJson(json).map { result ->
            if (!merge) {
                dao.deleteAllTasks()
                dao.deleteAllCompletions()
            }
            dao.insertTasks(result.tasks)
            for (comp in result.completions) {
                dao.insertOrReplaceCompletion(comp)
            }
            result.settings?.let { dao.insertOrUpdateSettings(it) }
        }
    }

    suspend fun clearAllData() {
        dao.deleteAllTasks()
        dao.deleteAllCompletions()
    }

    suspend fun resetToSampleData() {
        dao.deleteAllTasks()
        dao.deleteAllCompletions()
        LumaDatabase.populateInitialData(dao)
    }
}
