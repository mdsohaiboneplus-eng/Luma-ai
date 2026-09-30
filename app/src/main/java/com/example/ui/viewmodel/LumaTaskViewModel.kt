package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.LumaDatabase
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserSettingsEntity
import com.example.data.repository.StreakStats
import com.example.data.repository.TaskRepository
import com.example.model.Priority
import com.example.model.RecurrenceType
import com.example.model.Subtask
import com.example.model.TaskWithStatus
import com.example.util.DateUtils
import com.example.util.JsonUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LumaTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val database = LumaDatabase.getInstance(application)
        repository = TaskRepository(application, database.taskDao())
    }

    private val _todayDate = MutableStateFlow(DateUtils.getTodayString())
    val todayDate: StateFlow<String> = _todayDate.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow(DateUtils.getTodayString())
    val selectedCalendarDate: StateFlow<String> = _selectedCalendarDate.asStateFlow()

    val userSettings: StateFlow<UserSettingsEntity> = repository.userSettings
        .map { it ?: UserSettingsEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettingsEntity()
        )

    val todayTasks: StateFlow<List<TaskWithStatus>> = _todayDate
        .flatMapLatest { date -> repository.getTasksForDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val selectedDateTasks: StateFlow<List<TaskWithStatus>> = _selectedCalendarDate
        .flatMapLatest { date -> repository.getTasksForDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _streakStats = MutableStateFlow<StreakStats?>(null)
    val streakStats: StateFlow<StreakStats?> = _streakStats.asStateFlow()

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL") // ALL, ACTIVE, COMPLETED, HIGH_PRIORITY, RECURRING, TODAY
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow("TIME") // TIME, PRIORITY, DATE, TITLE
    val selectedSort: StateFlow<String> = _selectedSort.asStateFlow()

    private val _isAddSheetOpen = MutableStateFlow(false)
    val isAddSheetOpen: StateFlow<Boolean> = _isAddSheetOpen.asStateFlow()

    private val _editingTask = MutableStateFlow<TaskWithStatus?>(null)
    val editingTask: StateFlow<TaskWithStatus?> = _editingTask.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        refreshStats()
    }

    fun refreshStats() {
        viewModelScope.launch {
            val settings = userSettings.value
            val stats = repository.calculateStats(
                streakRequirement = settings.streakRequirement,
                dailyGoal = settings.dailyGoal
            )
            _streakStats.value = stats
        }
    }

    fun setActiveTab(index: Int) {
        _activeTab.value = index
        if (index == 3) {
            refreshStats()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setSelectedSort(sort: String) {
        _selectedSort.value = sort
    }

    fun setSelectedCalendarDate(date: String) {
        _selectedCalendarDate.value = date
    }

    fun openAddTask(targetDate: String? = null) {
        _editingTask.value = null
        targetDate?.let { _selectedCalendarDate.value = it }
        _isAddSheetOpen.value = true
    }

    fun openEditTask(task: TaskWithStatus) {
        _editingTask.value = task
        _isAddSheetOpen.value = true
    }

    fun closeAddOrEditSheet() {
        _isAddSheetOpen.value = false
        _editingTask.value = null
    }

    fun toggleTaskCompletion(taskId: Long, date: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId, date, isCompleted)
            refreshStats()
        }
    }

    fun saveTask(
        id: Long = 0,
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
    ) {
        viewModelScope.launch {
            val subtasksJson = JsonUtils.serializeSubtasks(subtasks)
            val taskEntity = TaskEntity(
                id = id,
                title = title.trim(),
                description = description.trim(),
                date = date,
                time = time?.ifBlank { null },
                priority = priority.name,
                recurrence = recurrence.name,
                customDays = customDays,
                reminderEnabled = reminderEnabled,
                reminderTime = reminderTime?.ifBlank { null },
                category = category,
                subtasksJson = subtasksJson
            )

            if (id == 0L) {
                repository.insertTask(taskEntity)
                showToast("Task created ✨")
            } else {
                repository.updateTask(taskEntity)
                showToast("Task updated ✨")
            }
            closeAddOrEditSheet()
            refreshStats()
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val entity = repository.allTasks.stateIn(viewModelScope).value.firstOrNull { it.id == taskId }
            if (entity != null) {
                repository.deleteTask(entity)
                showToast("Task deleted")
                refreshStats()
            }
        }
    }

    fun duplicateTask(taskId: Long) {
        viewModelScope.launch {
            val newId = repository.duplicateTask(taskId)
            if (newId > 0) {
                showToast("Task duplicated ✨")
                refreshStats()
            }
        }
    }

    fun updateSettings(newSettings: UserSettingsEntity) {
        viewModelScope.launch {
            repository.updateUserSettings(newSettings)
            showToast("Settings saved")
            refreshStats()
        }
    }

    suspend fun exportDataJson(): String {
        return repository.exportJson()
    }

    fun importDataJson(json: String, merge: Boolean = false, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.importJson(json, merge)
            if (result.isSuccess) {
                showToast("Data imported successfully! ✨")
                refreshStats()
                onResult(true, "Successfully imported")
            } else {
                val err = result.exceptionOrNull()?.message ?: "Invalid JSON data"
                onResult(false, err)
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            showToast("All data cleared")
            refreshStats()
        }
    }

    fun resetSampleData() {
        viewModelScope.launch {
            repository.resetToSampleData()
            showToast("Sample tasks restored ✨")
            refreshStats()
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
