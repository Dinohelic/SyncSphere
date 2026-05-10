package com.syncsphere.app.ui.tasks

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.syncsphere.app.models.TaskDto
import com.syncsphere.app.models.UpdateTaskRequest
import com.syncsphere.app.ui.common.formatCompactDate
import com.syncsphere.app.ui.common.priorityColor
import com.syncsphere.app.ui.common.statusColor
import com.syncsphere.app.ui.components.EmptyState
import com.syncsphere.app.ui.theme.Dimens
import com.syncsphere.app.utils.TokenManager
import com.syncsphere.app.viewmodel.TaskViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun TasksScreen(
    taskViewModel: TaskViewModel = hiltViewModel(),
    onAddTask: (() -> Unit)? = null,
    onEditTask: ((String) -> Unit)? = null
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState()
    val mutationState by taskViewModel.mutationState.collectAsState()
    val deleteState by taskViewModel.deleteState.collectAsState()
    val errorMessage by taskViewModel.errorMessage.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var pendingDeleteTask by remember { mutableStateOf<TaskDto?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val isAdmin = remember { TokenManager.getUserRole(context).equals("ADMIN", ignoreCase = true) }

    LaunchedEffect(Unit) {
        taskViewModel.getTasks()
    }

    LaunchedEffect(mutationState, deleteState, errorMessage) {
        when {
            deleteState?.isSuccess == true -> snackbarHostState.showSnackbar("Task deleted")
            mutationState?.isSuccess == true -> snackbarHostState.showSnackbar("Task updated")
            !errorMessage.isNullOrBlank() -> snackbarHostState.showSnackbar(errorMessage!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tasks") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.size(Dimens.spacing))

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
                                TaskCard(
                                    task = task,
                                    isAdmin = isAdmin,
                                    onEditTask = onEditTask,
                                    onRequestDelete = { pendingDeleteTask = task },
                                    onUpdateStatus = { status ->
                                        taskViewModel.updateTask(
                                            task.id,
                                            UpdateTaskRequest(status = status)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDeleteTask?.let { task ->
        AlertDialog(
            onDismissRequest = { pendingDeleteTask = null },
            title = { Text("Delete task?") },
            text = { Text("This action will remove ${task.title} permanently.") },
            confirmButton = {
                TextButton(onClick = {
                    taskViewModel.deleteTask(task.id)
                    pendingDeleteTask = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteTask = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun TaskCard(
    task: TaskDto,
    isAdmin: Boolean = false,
    onEditTask: ((String) -> Unit)? = null,
    onRequestDelete: (() -> Unit)? = null,
    onUpdateStatus: (String) -> Unit = {}
) {
    val status = task.status.uppercase().replace("PENDING", "TODO")
    val priority = task.priority.uppercase()
    var actionMenuExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { if (isAdmin) actionMenuExpanded = true }
            )
    ) {
        androidx.compose.material3.Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(Dimens.spacing)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                        task.description?.let {
                            Spacer(modifier = Modifier.size(Dimens.spacing_xs))
                            Text(text = it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (isAdmin) {
                        IconButton(onClick = { actionMenuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                    }
                }

                Spacer(modifier = Modifier.size(Dimens.spacing_sm))
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)) {
                    AssistChip(
                        onClick = { statusExpanded = true },
                        label = { Text(status.replace("_", " ")) },
                        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                            containerColor = statusColor(status).copy(alpha = 0.14f),
                            labelColor = statusColor(status)
                        )
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(priority) },
                        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                            containerColor = priorityColor(priority).copy(alpha = 0.14f),
                            labelColor = priorityColor(priority)
                        )
                    )
                }

                if (statusExpanded) {
                    DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                        listOf("TODO", "IN_PROGRESS", "COMPLETED").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.replace('_', ' ')) },
                                onClick = {
                                    statusExpanded = false
                                    if (option != status) onUpdateStatus(option)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.size(Dimens.spacing_sm))
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
                    val assigneeNames = task.assignedMembers.takeIf { it.isNotEmpty() }?.joinToString(", ") { it.fullName }
                        ?: task.assignedUser
                        ?: "Unassigned"
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = assigneeNames, style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (task.assignedMembers.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(Dimens.spacing_sm))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        task.assignedMembers.take(3).forEach { member ->
                            FilterChip(
                                selected = true,
                                onClick = {},
                                label = { Text(member.fullName) }
                            )
                        }
                    }
                }
            }
        }

        DropdownMenu(
            expanded = actionMenuExpanded,
            onDismissRequest = { actionMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                onClick = {
                    actionMenuExpanded = false
                    onEditTask?.invoke(task.id)
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                onClick = {
                    actionMenuExpanded = false
                    onRequestDelete?.invoke()
                }
            )
        }
    }
}
