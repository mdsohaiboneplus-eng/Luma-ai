package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.entity.TaskEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ReminderScheduler {

    fun scheduleTaskReminder(context: Context, task: TaskEntity) {
        if (!task.reminderEnabled || task.reminderTime.isNullOrBlank()) {
            cancelTaskReminder(context, task.id)
            return
        }

        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, TaskReminderReceiver::class.java).apply {
                putExtra(TaskReminderReceiver.EXTRA_TASK_ID, task.id)
                putExtra(TaskReminderReceiver.EXTRA_TASK_TITLE, task.title)
                putExtra(TaskReminderReceiver.EXTRA_TASK_DESC, task.description.ifBlank { "Reminder for ${task.title}" })
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val timeParts = task.reminderTime.split(":")
            val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 9
            val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                // If today's time has already passed, schedule for tomorrow
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelTaskReminder(context: Context, taskId: Long) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, TaskReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerTestNotification(context: Context) {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TASK_ID, 9999L)
            putExtra(TaskReminderReceiver.EXTRA_TASK_TITLE, "LumaTask Reminder Test")
            putExtra(TaskReminderReceiver.EXTRA_TASK_DESC, "Liquid glass reminders are active and ready!")
        }
        context.sendBroadcast(intent)
    }
}
