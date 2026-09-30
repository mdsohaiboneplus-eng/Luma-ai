package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Priority
import com.example.model.RecurrenceEvaluator
import com.example.model.RecurrenceType
import com.example.model.Subtask
import com.example.util.JsonUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LumaTask", appName)
  }

  @Test
  fun `test recurring task evaluation`() {
    // Daily task created 2026-09-30 should be scheduled on 2026-09-30 and 2026-10-01
    assertTrue(
      RecurrenceEvaluator.isTaskScheduledOnDate(
        recurrence = RecurrenceType.DAILY,
        customDays = "",
        baseDateStr = "2026-09-30",
        targetDateStr = "2026-10-01"
      )
    )

    // A task cannot appear before its creation date
    assertFalse(
      RecurrenceEvaluator.isTaskScheduledOnDate(
        recurrence = RecurrenceType.DAILY,
        customDays = "",
        baseDateStr = "2026-09-30",
        targetDateStr = "2026-09-29"
      )
    )

    // Non-repeating task should only appear on its base date
    assertTrue(
      RecurrenceEvaluator.isTaskScheduledOnDate(
        recurrence = RecurrenceType.NONE,
        customDays = "",
        baseDateStr = "2026-09-30",
        targetDateStr = "2026-09-30"
      )
    )
    assertFalse(
      RecurrenceEvaluator.isTaskScheduledOnDate(
        recurrence = RecurrenceType.NONE,
        customDays = "",
        baseDateStr = "2026-09-30",
        targetDateStr = "2026-10-01"
      )
    )
  }

  @Test
  fun `test subtask serialization and deserialization`() {
    val subtasks = listOf(
      Subtask(id = "1", title = "Drink glass 1", isCompleted = true),
      Subtask(id = "2", title = "Drink glass 2", isCompleted = false)
    )
    val json = JsonUtils.serializeSubtasks(subtasks)
    val parsed = JsonUtils.parseSubtasks(json)

    assertEquals(2, parsed.size)
    assertEquals("Drink glass 1", parsed[0].title)
    assertTrue(parsed[0].isCompleted)
    assertEquals("Drink glass 2", parsed[1].title)
    assertFalse(parsed[1].isCompleted)
  }
}
