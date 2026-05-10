package com.syncsphere.app.ui.tasks

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.syncsphere.app.ui.components.EmptyState
import com.syncsphere.app.ui.common.formatCompactDate
import com.syncsphere.app.ui.common.priorityColor
import com.syncsphere.app.ui.common.statusColor
import com.syncsphere.app.ui.theme.Dimens
import com.syncsphere.app.utils.TokenManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import com.syncsphere.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    taskViewModel: TaskViewModel = hiltViewModel(),
    onAddTask: (() -> Unit)? = null
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isAdmin = remember { TokenManager.getUserRole(context).equals("ADMIN", ignoreCase = true) }

    LaunchedEffect(Unit) {
        taskViewModel.getTasks()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tasks") })
        },
        floatingActionButton = {
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    text = { Text("New Task") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    onClick = { onAddTask?.invoke() }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimens.spacing)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search tasks") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(Dimens.spacing))

            Crossfade(targetState = isLoading, label = "tasks_loading_transition") { loading ->
                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val source = tasks?.getOrNull().orEmpty()
                    val filtered = source.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                            (it.description?.contains(searchQuery, ignoreCase = true) == true)
                    }

                    if (filtered.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            EmptyState(
                                title = "Nothing to show yet",
                                subtitle = "Try another keyword or create a fresh task."
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)) {
                            items(filtered, key = { it.id }) { task ->
                                TaskCard(task = task)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: com.syncsphere.app.models.TaskDto) {
    val status = task.status.uppercase().replace("PENDING", "TODO")
    val priority = task.priority.uppercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(Dimens.spacing)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            task.description?.let {
                Spacer(modifier = Modifier.height(Dimens.spacing_xs))
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)) {
                AssistChip(
                    onClick = {},
                    label = { Text(status.replace("_", " ")) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = statusColor(status).copy(alpha = 0.14f),
                        labelColor = statusColor(status)
                    )
                )
                AssistChip(
                    onClick = {},
                    label = { Text(priority) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = priorityColor(priority).copy(alpha = 0.14f),
                        labelColor = priorityColor(priority)
                    )
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = formatCompactDate(task.dueDate), style = MaterialTheme.typography.bodySmall)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = task.assignedUser ?: task.assignedTo?.fullName ?: "Unassigned",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

