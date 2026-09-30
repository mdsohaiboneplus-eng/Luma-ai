package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.entity.TaskCompletionEntity
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        TaskEntity::class,
        TaskCompletionEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LumaDatabase : RoomDatabase() {

    abstract fun taskDao(): LumaTaskDao

    companion object {
        @Volatile
        private var INSTANCE: LumaDatabase? = null

        fun getInstance(context: Context): LumaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LumaDatabase::class.java,
                    "lumatask_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default sample tasks on background thread
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    populateInitialData(database.taskDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(dao: LumaTaskDao) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

            val defaultSettings = UserSettingsEntity(
                id = 1,
                userName = "Sohaib",
                userAvatar = "✨",
                themeMode = "LIQUID_DARK",
                accentColor = "VIOLET",
                streakRequirement = "ALL_TASKS",
                dailyGoal = 5,
                notificationsEnabled = true,
                onboardingCompleted = true
            )
            dao.insertOrUpdateSettings(defaultSettings)

            val sampleTasks = listOf(
                TaskEntity(
                    title = "Morning workout",
                    description = "20 min full body stretch & mobility session",
                    date = today,
                    time = "07:30",
                    priority = "HIGH",
                    recurrence = "DAILY",
                    reminderEnabled = true,
                    reminderTime = "07:15",
                    category = "Health",
                    subtasksJson = "[{\"id\":\"1\",\"title\":\"5 min warmup\",\"isCompleted\":true},{\"id\":\"2\",\"title\":\"Main sets\",\"isCompleted\":false}]"
                ),
                TaskEntity(
                    title = "Drink water",
                    description = "Stay hydrated - 500ml morning glass",
                    date = today,
                    time = "08:00",
                    priority = "MEDIUM",
                    recurrence = "DAILY",
                    reminderEnabled = false,
                    category = "Health",
                    subtasksJson = "[]"
                ),
                TaskEntity(
                    title = "Study 2 hours",
                    description = "Deep focus session on algorithms & systems design",
                    date = today,
                    time = "14:00",
                    priority = "HIGH",
                    recurrence = "WEEKDAYS",
                    reminderEnabled = true,
                    reminderTime = "13:55",
                    category = "Study",
                    subtasksJson = "[{\"id\":\"1\",\"title\":\"Module 1 review\",\"isCompleted\":false},{\"id\":\"2\",\"title\":\"Practice problems\",\"isCompleted\":false}]"
                ),
                TaskEntity(
                    title = "Read Quran",
                    description = "Daily recitation and reflection with translation",
                    date = today,
                    time = "18:30",
                    priority = "HIGH",
                    recurrence = "DAILY",
                    reminderEnabled = true,
                    reminderTime = "18:25",
                    category = "Faith",
                    subtasksJson = "[]"
                ),
                TaskEntity(
                    title = "Revision & planning",
                    description = "Review progress for the day and organize tomorrow's priorities",
                    date = today,
                    time = "21:00",
                    priority = "MEDIUM",
                    recurrence = "DAILY",
                    reminderEnabled = false,
                    category = "Personal",
                    subtasksJson = "[]"
                )
            )

            dao.insertTasks(sampleTasks)
        }
    }
}
