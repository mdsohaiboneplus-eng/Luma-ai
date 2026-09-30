package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.TaskCompletionEntity
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LumaTaskDao {

    @Query("SELECT * FROM tasks WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isArchived = 0 ORDER BY createdAt DESC")
    suspend fun getAllTasksList(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("SELECT * FROM task_completions")
    fun getAllCompletions(): Flow<List<TaskCompletionEntity>>

    @Query("SELECT * FROM task_completions")
    suspend fun getAllCompletionsList(): List<TaskCompletionEntity>

    @Query("SELECT * FROM task_completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<TaskCompletionEntity>>

    @Query("SELECT * FROM task_completions WHERE taskId = :taskId AND date = :date LIMIT 1")
    suspend fun getCompletion(taskId: Long, date: String): TaskCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceCompletion(completion: TaskCompletionEntity)

    @Query("DELETE FROM task_completions WHERE taskId = :taskId AND date = :date")
    suspend fun deleteCompletion(taskId: Long, date: String)

    @Query("DELETE FROM task_completions WHERE taskId = :taskId")
    suspend fun deleteAllCompletionsForTask(taskId: Long)

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getUserSettingsDirect(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: UserSettingsEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM task_completions")
    suspend fun deleteAllCompletions()
}
