package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String, // Scheduled base date YYYY-MM-DD
    val time: String? = null, // HH:mm format e.g. "08:30"
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH
    val recurrence: String = "NONE", // NONE, DAILY, WEEKDAYS, WEEKENDS, WEEKLY, MONTHLY, CUSTOM
    val customDays: String = "", // e.g. "2,3,4" (Monday..Wednesday)
    val reminderEnabled: Boolean = false,
    val reminderTime: String? = null,
    val category: String = "Personal",
    val subtasksJson: String = "[]",
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "task_completions",
    indices = [Index(value = ["taskId", "date"], unique = true)]
)
data class TaskCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "Sohaib",
    val userAvatar: String = "✨",
    val themeMode: String = "LIQUID_DARK", // LIQUID_DARK, DEEP_MIDNIGHT, GLASS_LIGHT
    val accentColor: String = "VIOLET", // VIOLET, CYAN, EMERALD, AMBER
    val streakRequirement: String = "ALL_TASKS", // ALL_TASKS, AT_LEAST_ONE
    val dailyGoal: Int = 5,
    val notificationsEnabled: Boolean = true,
    val onboardingCompleted: Boolean = true
)
