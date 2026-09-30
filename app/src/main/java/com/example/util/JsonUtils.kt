package com.example.util

import com.example.data.entity.TaskCompletionEntity
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserSettingsEntity
import com.example.model.Subtask
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object JsonUtils {

    fun parseSubtasks(json: String?): List<Subtask> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<Subtask>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Subtask(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        title = obj.optString("title", ""),
                        isCompleted = obj.optBoolean("isCompleted", false)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun serializeSubtasks(subtasks: List<Subtask>): String {
        val array = JSONArray()
        for (sub in subtasks) {
            val obj = JSONObject().apply {
                put("id", sub.id)
                put("title", sub.title)
                put("isCompleted", sub.isCompleted)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun exportToJson(
        tasks: List<TaskEntity>,
        completions: List<TaskCompletionEntity>,
        settings: UserSettingsEntity?
    ): String {
        val root = JSONObject()
        root.put("app", "LumaTask")
        root.put("version", "1.0.0")
        root.put("exportedAt", System.currentTimeMillis())

        val tasksArray = JSONArray()
        for (task in tasks) {
            val taskObj = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("date", task.date)
                put("time", task.time ?: JSONObject.NULL)
                put("priority", task.priority)
                put("recurrence", task.recurrence)
                put("customDays", task.customDays)
                put("reminderEnabled", task.reminderEnabled)
                put("reminderTime", task.reminderTime ?: JSONObject.NULL)
                put("category", task.category)
                put("subtasksJson", task.subtasksJson)
                put("createdAt", task.createdAt)
            }
            tasksArray.put(taskObj)
        }
        root.put("tasks", tasksArray)

        val compArray = JSONArray()
        for (comp in completions) {
            val compObj = JSONObject().apply {
                put("taskId", comp.taskId)
                put("date", comp.date)
                put("isCompleted", comp.isCompleted)
                put("completedAt", comp.completedAt)
            }
            compArray.put(compObj)
        }
        root.put("completions", compArray)

        if (settings != null) {
            val settingsObj = JSONObject().apply {
                put("userName", settings.userName)
                put("userAvatar", settings.userAvatar)
                put("themeMode", settings.themeMode)
                put("accentColor", settings.accentColor)
                put("streakRequirement", settings.streakRequirement)
                put("dailyGoal", settings.dailyGoal)
                put("notificationsEnabled", settings.notificationsEnabled)
            }
            root.put("settings", settingsObj)
        }

        return root.toString(2)
    }

    data class ImportResult(
        val tasks: List<TaskEntity>,
        val completions: List<TaskCompletionEntity>,
        val settings: UserSettingsEntity?
    )

    fun importFromJson(jsonString: String): Result<ImportResult> {
        return runCatching {
            val root = JSONObject(jsonString)
            val tasksList = mutableListOf<TaskEntity>()
            val compList = mutableListOf<TaskCompletionEntity>()

            if (root.has("tasks")) {
                val array = root.getJSONArray("tasks")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    tasksList.add(
                        TaskEntity(
                            id = if (obj.has("id")) obj.getLong("id") else 0,
                            title = obj.getString("title"),
                            description = obj.optString("description", ""),
                            date = obj.getString("date"),
                            time = if (obj.isNull("time")) null else obj.optString("time"),
                            priority = obj.optString("priority", "MEDIUM"),
                            recurrence = obj.optString("recurrence", "NONE"),
                            customDays = obj.optString("customDays", ""),
                            reminderEnabled = obj.optBoolean("reminderEnabled", false),
                            reminderTime = if (obj.isNull("reminderTime")) null else obj.optString("reminderTime"),
                            category = obj.optString("category", "Personal"),
                            subtasksJson = obj.optString("subtasksJson", "[]"),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            if (root.has("completions")) {
                val array = root.getJSONArray("completions")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    compList.add(
                        TaskCompletionEntity(
                            taskId = obj.getLong("taskId"),
                            date = obj.getString("date"),
                            isCompleted = obj.getBoolean("isCompleted"),
                            completedAt = obj.optLong("completedAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            var settings: UserSettingsEntity? = null
            if (root.has("settings")) {
                val obj = root.getJSONObject("settings")
                settings = UserSettingsEntity(
                    id = 1,
                    userName = obj.optString("userName", "Sohaib"),
                    userAvatar = obj.optString("userAvatar", "✨"),
                    themeMode = obj.optString("themeMode", "LIQUID_DARK"),
                    accentColor = obj.optString("accentColor", "VIOLET"),
                    streakRequirement = obj.optString("streakRequirement", "ALL_TASKS"),
                    dailyGoal = obj.optInt("dailyGoal", 5),
                    notificationsEnabled = obj.optBoolean("notificationsEnabled", true),
                    onboardingCompleted = true
                )
            }

            ImportResult(tasksList, compList, settings)
        }
    }
}
