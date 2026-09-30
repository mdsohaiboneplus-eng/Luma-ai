package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.UserSettingsEntity
import com.example.reminder.ReminderScheduler
import com.example.ui.components.LiquidGlassButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LumaAmber
import com.example.ui.theme.LumaCyan
import com.example.ui.theme.LumaEmerald
import com.example.ui.theme.LumaViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settings: UserSettingsEntity,
    onUpdateSettings: (UserSettingsEntity) -> Unit,
    onExportData: suspend () -> String,
    onImportData: (String, Boolean, (Boolean, String) -> Unit) -> Unit,
    onClearAllData: () -> Unit,
    onResetSampleData: () -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonInput by remember { mutableStateOf("") }
    var importError by remember { mutableStateOf<String?>(null) }
    var showWidgetInfoDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Settings",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Customization, notifications, and data management",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Profile Section
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    SectionHeader(title = "Profile", icon = Icons.Default.Person)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar picker
                        val avatars = listOf("✨", "🚀", "🧘", "💼", "🧠", "⚡", "🌟")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            avatars.forEach { av ->
                                val isSelected = settings.userAvatar == av
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0x18FFFFFF))
                                        .border(1.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
                                        .clickable { onUpdateSettings(settings.copy(userAvatar = av)) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = av, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = settings.userName,
                        onValueChange = { onUpdateSettings(settings.copy(userName = it)) },
                        label = { Text("Display Name", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = LiquidGlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_name_input")
                    )
                }
            }
        }

        // Appearance & Themes
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    SectionHeader(title = "Appearance & Style", icon = Icons.Default.Palette)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Theme Style", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    val themes = listOf(
                        "LIQUID_DARK" to "Liquid Dark",
                        "DEEP_MIDNIGHT" to "Midnight",
                        "GLASS_LIGHT" to "Glass Light"
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        themes.forEach { (mode, label) ->
                            val isSelected = settings.themeMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0x18FFFFFF))
                                    .clickable { onUpdateSettings(settings.copy(themeMode = mode)) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Accent Color", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    val accents = listOf(
                        "VIOLET" to LumaViolet,
                        "CYAN" to LumaCyan,
                        "EMERALD" to LumaEmerald,
                        "AMBER" to LumaAmber
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        accents.forEach { (name, color) ->
                            val isSelected = settings.accentColor.equals(name, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onUpdateSettings(settings.copy(accentColor = name)) }
                            )
                        }
                    }
                }
            }
        }

        // Notifications & Reminders
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    SectionHeader(title = "Reminders & Notifications", icon = Icons.Default.Notifications)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Task Notifications", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Push reminders for scheduled tasks", color = TextMuted, fontSize = 12.sp)
                        }

                        Switch(
                            checked = settings.notificationsEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(notificationsEnabled = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LiquidGlassButton(
                        text = "🔔 Send Test Notification",
                        isSecondary = true,
                        onClick = {
                            ReminderScheduler.triggerTestNotification(context)
                            onShowToast("Test notification sent!")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Productivity & Streak Goals
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    SectionHeader(title = "Productivity Goals", icon = Icons.Default.Info)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Streak Calculation Rule", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "ALL_TASKS" to "All Daily Tasks",
                            "AT_LEAST_ONE" to "At Least 1 Task"
                        ).forEach { (req, label) ->
                            val isSelected = settings.streakRequirement == req
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0x18FFFFFF))
                                    .clickable { onUpdateSettings(settings.copy(streakRequirement = req)) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Daily Goal: ${settings.dailyGoal} tasks / day", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(3, 5, 7, 10).forEach { goal ->
                            val isSelected = settings.dailyGoal == goal
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0x18FFFFFF))
                                    .clickable { onUpdateSettings(settings.copy(dailyGoal = goal)) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$goal",
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Android Companion Widget Architecture Info
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showWidgetInfoDialog = true },
                backgroundColor = Color(0x1F3B82F6)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Android Home-Screen Widget Architecture", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Learn how native Android companion widgets connect to LumaTask", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Backup, Restore & Data
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    SectionHeader(title = "Data & Backups", icon = Icons.Default.CloudUpload)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiquidGlassButton(
                            text = "Export JSON",
                            icon = { Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                scope.launch {
                                    exportedJsonText = onExportData()
                                    showExportDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        LiquidGlassButton(
                            text = "Import JSON",
                            isSecondary = true,
                            icon = { Icon(Icons.Default.CloudDownload, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                importJsonInput = ""
                                importError = null
                                showImportDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiquidGlassButton(
                            text = "Restore Samples",
                            isSecondary = true,
                            icon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp)) },
                            onClick = onResetSampleData,
                            modifier = Modifier.weight(1f)
                        )

                        LiquidGlassButton(
                            text = "Clear All Data",
                            isSecondary = true,
                            icon = { Icon(Icons.Default.CleaningServices, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp)) },
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // About LumaTask
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text("LumaTask", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Version 1.0.0 • Liquid Glass Edition", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Local-first personal productivity system with daily tasks, recurring habit occurrences, intelligent streak analytics, and alarm-based reminders.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    // Clear Data Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All Data?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove all tasks, completion history, and streak records. Are you sure?", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllData()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("Delete Everything", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Data (JSON)", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Your complete tasks and completion history formatted as JSON:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportedJsonText,
                        onValueChange = {},
                        readOnly = true,
                        maxLines = 8,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("LumaTask Backup", exportedJsonText)
                        clipboard.setPrimaryClip(clip)
                        onShowToast("JSON copied to clipboard! ✨")
                        showExportDialog = false
                    }
                ) {
                    Text("Copy to Clipboard", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = TextPrimary)
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Data (JSON)", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Paste your valid LumaTask JSON backup below:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = {
                            importJsonInput = it
                            importError = null
                        },
                        placeholder = { Text("Paste JSON here...", color = TextMuted) },
                        maxLines = 8,
                        isError = importError != null,
                        supportingText = if (importError != null) {
                            { Text(importError!!, color = MaterialTheme.colorScheme.error) }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (importJsonInput.isBlank()) {
                            importError = "Please paste JSON content"
                        } else {
                            onImportData(importJsonInput, false) { success, msg ->
                                if (success) {
                                    showImportDialog = false
                                } else {
                                    importError = msg
                                }
                            }
                        }
                    }
                ) {
                    Text("Import & Restore", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    // Widget Documentation Dialog
    if (showWidgetInfoDialog) {
        AlertDialog(
            onDismissRequest = { showWidgetInfoDialog = false },
            title = { Text("Android Widget Architecture", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "LumaTask is designed with a clean local-first Room database.\n\n" +
                                "To connect an Android Home-Screen Widget (AppWidgetProvider / Glance):\n" +
                                "1. An AppWidgetProvider queries `LumaDatabase.getInstance(context).taskDao().getAllTasks()`.\n" +
                                "2. It calls `RecurrenceEvaluator.isTaskScheduledOnDate(...)` for today's date.\n" +
                                "3. The widget renders today's checklist directly on the Android home screen launcher!\n" +
                                "4. Interactive widget taps update `TaskCompletionEntity` through the database with automatic broadcast refresh.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showWidgetInfoDialog = false }) {
                    Text("Got it", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
