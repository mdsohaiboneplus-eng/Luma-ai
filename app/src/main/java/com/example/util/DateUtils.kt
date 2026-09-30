package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val headerFormat = SimpleDateFormat("EEEE, MMMM d", Locale.US)
    private val shortDateFormat = SimpleDateFormat("MMM d", Locale.US)
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.US)

    fun getTodayString(): String {
        return isoFormat.format(Date())
    }

    fun formatHeaderDate(dateStr: String): String {
        return try {
            val date = isoFormat.parse(dateStr) ?: Date()
            headerFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatShortDate(dateStr: String): String {
        return try {
            val date = isoFormat.parse(dateStr) ?: Date()
            shortDateFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatMonthYear(calendar: Calendar): String {
        return monthYearFormat.format(calendar.time)
    }

    fun getGreeting(userName: String = ""): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning"
            hour in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
        return if (userName.isNotBlank()) "$greeting, $userName" else greeting
    }

    fun isToday(dateStr: String): Boolean {
        return dateStr == getTodayString()
    }

    fun addDays(dateStr: String, days: Int): String {
        return try {
            val cal = Calendar.getInstance().apply {
                time = isoFormat.parse(dateStr) ?: Date()
                add(Calendar.DAY_OF_YEAR, days)
            }
            isoFormat.format(cal.time)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getPastDays(count: Int): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -(count - 1))
        for (i in 0 until count) {
            list.add(isoFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    fun getDayOfWeekLabel(dateStr: String): String {
        return try {
            val cal = Calendar.getInstance().apply {
                time = isoFormat.parse(dateStr) ?: Date()
            }
            SimpleDateFormat("EEE", Locale.US).format(cal.time)
        } catch (e: Exception) {
            ""
        }
    }
}
