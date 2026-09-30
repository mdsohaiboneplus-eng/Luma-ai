package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Priority
import com.example.model.RecurrenceType
import com.example.model.TaskWithStatus
import com.example.ui.components.AddOrEditTaskSheet
import com.example.ui.components.LiquidBottomNav
import com.example.ui.components.OnboardingDialog
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.LiquidDarkBackground
import com.example.ui.theme.LumaTaskTheme
import com.example.ui.viewmodel.LumaTaskViewModel
import com.example.util.DateUtils

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: LumaTaskViewModel = viewModel()
            val settings by viewModel.userSettings.collectAsStateWithLifecycle()

            LumaTaskTheme(
                themeMode = settings.themeMode,
                accentName = settings.accentColor
            ) {
                LumaTaskApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LumaTaskApp(viewModel: LumaTaskViewModel) {
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val selectedDateTasks by viewModel.selectedDateTasks.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val streakStats by viewModel.streakStats.collectAsStateWithLifecycle()

    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val isAddSheetOpen by viewModel.isAddSheetOpen.collectAsStateWithLifecycle()
    val editingTask by viewModel.editingTask.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Toast handler
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    // Android 13+ Notification Permission Launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.showToast("Notifications enabled ✨")
        }
    }

    // Back button handling: return to Today tab if not on Today
    if (activeTab != 0) {
        BackHandler {
            viewModel.setActiveTab(0)
        }
    }

    // Convert allTasks to TaskWithStatus for the Tasks screen
    val allTasksWithStatus = remember(allTasks, todayTasks) {
        val todayCompMap = todayTasks.associate { it.id to it.isCompletedForDate }
        allTasks.map { task ->
            TaskWithStatus(
                id = task.id,
                title = task.title,
                description = task.description,
                date = task.date,
                targetDate = DateUtils.getTodayString(),
                time = task.time,
                priority = Priority.fromString(task.priority),
                recurrence = RecurrenceType.fromString(task.recurrence),
                customDays = task.customDays,
                reminderEnabled = task.reminderEnabled,
                reminderTime = task.reminderTime,
                category = task.category,
                subtasks = emptyList(),
                isCompletedForDate = todayCompMap[task.id] == true,
                completedAt = null,
                isArchived = task.isArchived,
                createdAt = task.createdAt
            )
        }
    }

    // Liquid Glass midnight ambient glow background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        Color(0xFF090D17),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Subtle ambient glow accent
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 1200f
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                LiquidBottomNav(
                    selectedTab = activeTab,
                    onTabSelected = { viewModel.setActiveTab(it) }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                // Responsive Desktop / Tablet container constraint
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 680.dp)
                ) {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tab_transition"
                    ) { tabIndex ->
                        when (tabIndex) {
                            0 -> TodayScreen(
                                tasks = todayTasks,
                                userSettings = settings,
                                streakStats = streakStats,
                                onToggleTask = { id, done -> viewModel.toggleTaskCompletion(id, DateUtils.getTodayString(), done) },
                                onEditTask = { viewModel.openEditTask(it) },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                onDuplicateTask = { viewModel.duplicateTask(it) },
                                onAddTask = { viewModel.openAddTask(DateUtils.getTodayString()) }
                            )

                            1 -> TasksScreen(
                                tasks = allTasksWithStatus,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                selectedFilter = selectedFilter,
                                onFilterChange = { viewModel.setSelectedFilter(it) },
                                selectedSort = selectedSort,
                                onSortChange = { viewModel.setSelectedSort(it) },
                                onToggleTask = { id, done -> viewModel.toggleTaskCompletion(id, DateUtils.getTodayString(), done) },
                                onEditTask = { viewModel.openEditTask(it) },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                onDuplicateTask = { viewModel.duplicateTask(it) },
                                onAddTask = { viewModel.openAddTask(DateUtils.getTodayString()) }
                            )

                            2 -> CalendarScreen(
                                selectedDate = selectedCalendarDate,
                                onSelectDate = { viewModel.setSelectedCalendarDate(it) },
                                dayTasks = selectedDateTasks,
                                onToggleTask = { id, done -> viewModel.toggleTaskCompletion(id, selectedCalendarDate, done) },
                                onEditTask = { viewModel.openEditTask(it) },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                onDuplicateTask = { viewModel.duplicateTask(it) },
                                onAddTask = { viewModel.openAddTask(it) }
                            )

                            3 -> ProgressScreen(
                                stats = streakStats,
                                userSettings = settings
                            )

                            4 -> SettingsScreen(
                                settings = settings,
                                onUpdateSettings = { viewModel.updateSettings(it) },
                                onExportData = { viewModel.exportDataJson() },
                                onImportData = { json, merge, callback -> viewModel.importDataJson(json, merge, callback) },
                                onClearAllData = { viewModel.clearAllData() },
                                onResetSampleData = { viewModel.resetSampleData() },
                                onShowToast = { viewModel.showToast(it) }
                            )
                        }
                    }
                }
            }
        }

        // Add or Edit Task Bottom Sheet
        if (isAddSheetOpen) {
            AddOrEditTaskSheet(
                initialDate = selectedCalendarDate,
                taskToEdit = editingTask,
                onDismiss = { viewModel.closeAddOrEditSheet() },
                onSave = { id, title, desc, date, time, priority, rec, customDays, reminder, reminderTime, category, subtasks ->
                    viewModel.saveTask(
                        id = id,
                        title = title,
                        description = desc,
                        date = date,
                        time = time,
                        priority = priority,
                        recurrence = rec,
                        customDays = customDays,
                        reminderEnabled = reminder,
                        reminderTime = reminderTime,
                        category = category,
                        subtasks = subtasks
                    )
                }
            )
        }

        // First-run Onboarding Dialog if not completed
        if (!settings.onboardingCompleted) {
            OnboardingDialog(
                onRequestNotificationPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.showToast("Notifications ready")
                    }
                },
                onComplete = { userName, firstTaskTitle ->
                    viewModel.updateSettings(
                        settings.copy(
                            userName = userName,
                            onboardingCompleted = true
                        )
                    )
                    if (!firstTaskTitle.isNullOrBlank()) {
                        viewModel.saveTask(
                            title = firstTaskTitle,
                            description = "Created during onboarding",
                            date = DateUtils.getTodayString(),
                            time = "09:00",
                            priority = Priority.HIGH,
                            recurrence = RecurrenceType.DAILY,
                            customDays = "",
                            reminderEnabled = true,
                            reminderTime = "08:50",
                            category = "Personal",
                            subtasks = emptyList()
                        )
                    }
                }
            )
        }
    }
}
