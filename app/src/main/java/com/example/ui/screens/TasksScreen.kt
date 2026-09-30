package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Priority
import com.example.model.RecurrenceType
import com.example.model.TaskWithStatus
import com.example.ui.components.LiquidGlassButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.TaskItemRow
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TasksScreen(
    tasks: List<TaskWithStatus>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    selectedSort: String,
    onSortChange: (String) -> Unit,
    onToggleTask: (Long, Boolean) -> Unit,
    onEditTask: (TaskWithStatus) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onDuplicateTask: (Long) -> Unit,
    onAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    val filters = listOf(
        "ALL" to "All",
        "ACTIVE" to "Active",
        "COMPLETED" to "Completed",
        "HIGH_PRIORITY" to "High Priority",
        "RECURRING" to "Recurring",
        "TODAY" to "Today"
    )

    // Filter Logic
    val filteredTasks = tasks.filter { task ->
        val matchesSearch = searchQuery.isBlank() ||
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true) ||
                task.category.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "ACTIVE" -> !task.isCompletedForDate
            "COMPLETED" -> task.isCompletedForDate
            "HIGH_PRIORITY" -> task.priority == Priority.HIGH
            "RECURRING" -> task.recurrence != RecurrenceType.NONE
            "TODAY" -> task.date == task.targetDate
            else -> true
        }

        matchesSearch && matchesFilter
    }.sortedWith { a, b ->
        when (selectedSort) {
            "PRIORITY" -> {
                val weightA = when (a.priority) { Priority.HIGH -> 3; Priority.MEDIUM -> 2; Priority.LOW -> 1 }
                val weightB = when (b.priority) { Priority.HIGH -> 3; Priority.MEDIUM -> 2; Priority.LOW -> 1 }
                weightB.compareTo(weightA)
            }
            "DATE" -> b.createdAt.compareTo(a.createdAt)
            "TITLE" -> a.title.compareTo(b.title, ignoreCase = true)
            else -> {
                // TIME
                val timeA = a.time ?: "99:99"
                val timeB = b.time ?: "99:99"
                timeA.compareTo(timeB)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Screen Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All Tasks",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )

                // Sort Dropdown Button
                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x18FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort tasks",
                            tint = TextPrimary
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by Time") },
                            onClick = { onSortChange("TIME"); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Priority") },
                            onClick = { onSortChange("PRIORITY"); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Date Added") },
                            onClick = { onSortChange("DATE"); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Title") },
                            onClick = { onSortChange("TITLE"); showSortMenu = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search tasks, notes, or categories...", color = TextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = LiquidGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_search_field")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color(0x18FFFFFF)
                            )
                            .clickable { onFilterChange(key) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("filter_chip_$key")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Task List
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Inbox,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching tasks",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Try changing your search term" else "Add tasks or switch filters",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LiquidGlassButton(
                            text = "+ Create Task",
                            onClick = onAddTask
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "${filteredTasks.size} tasks",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    items(filteredTasks, key = { it.id }) { task ->
                        TaskItemRow(
                            task = task,
                            onToggleComplete = { onToggleTask(task.id, it) },
                            onEdit = { onEditTask(task) },
                            onDelete = { onDeleteTask(task.id) },
                            onDuplicate = { onDuplicateTask(task.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddTask,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp)
                .testTag("tasks_fab_add_task")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
