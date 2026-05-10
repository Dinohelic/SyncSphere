package com.syncsphere.app.ui.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.syncsphere.app.models.CreateTaskRequest
import com.syncsphere.app.models.TaskDto
import com.syncsphere.app.models.UpdateTaskRequest
import com.syncsphere.app.models.UserResponse
import com.syncsphere.app.viewmodel.TaskViewModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskFormScreen(
    navController: NavController,
    taskId: String? = null,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("MEDIUM") }
    var selectedStatus by remember { mutableStateOf("TODO") }
    var selectedDueDateIso by remember { mutableStateOf<String?>(null) }
    var selectedDueDisplay by remember { mutableStateOf("") }
    val selectedMembers = remember { mutableStateListOf<UserResponse>() }

    val tasks by taskViewModel.tasks.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState()
    val usersResult by taskViewModel.users.collectAsState()
    val usersLoading by taskViewModel.isUsersLoading.collectAsState()
    val users = usersResult?.getOrNull().orEmpty()
    val errorMessage by taskViewModel.errorMessage.collectAsState()
    val mutationState by taskViewModel.mutationState.collectAsState()
    val createState by taskViewModel.createTaskState.collectAsState()

    var priorityExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var assigneeExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var hasPrefilled by remember(taskId) { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val displayFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()) }
    val isEditMode = !taskId.isNullOrBlank()
    val currentTask = tasks?.getOrNull()?.firstOrNull { it.id == taskId }

    val titleError = title.isBlank()
    val assigneeError = selectedMembers.isEmpty()
    val isFormValid = title.isNotBlank() && selectedMembers.isNotEmpty()

    LaunchedEffect(Unit) {
        taskViewModel.getUsers()
        if (isEditMode) {
            taskViewModel.getTasks()
        }
    }

    LaunchedEffect(currentTask?.id) {
        val task = currentTask ?: return@LaunchedEffect
        if (!hasPrefilled) {
            title = task.title
            description = task.description.orEmpty()
            selectedPriority = task.priority.uppercase()
            selectedStatus = task.status.uppercase()
            selectedDueDateIso = task.dueDate
            selectedDueDisplay = task.dueDate?.let {
                try {
                    OffsetDateTime.parse(it).format(displayFormatter)
                } catch (_: Exception) {
                    it.take(10)
                }
            }.orEmpty()
            selectedMembers.clear()
            if (task.assignedMembers.isNotEmpty()) {
                selectedMembers.addAll(task.assignedMembers.map {
                    UserResponse(it.id, it.fullName, it.email, it.role)
                })
            } else if (task.assignedToId != null && task.assignedTo != null) {
                selectedMembers.add(
                    UserResponse(
                        id = task.assignedToId,
                        fullName = task.assignedTo.fullName.orEmpty(),
                        email = task.assignedTo.email.orEmpty(),
                        role = "MEMBER"
                    )
                )
            }
            hasPrefilled = true
        }
    }

    LaunchedEffect(createState, mutationState) {
        if (mutationState?.getOrNull() == null && createState?.getOrNull() == null) return@LaunchedEffect
        snackbarHostState.showSnackbar(if (isEditMode) "Task updated" else "Task created")
        taskViewModel.clearTaskStates()
        taskViewModel.getTasks()
        navController.popBackStack()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            taskViewModel.clearTaskStates()
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        if (selected != null) {
                            val selectedDate = Instant.ofEpochMilli(selected).atZone(ZoneOffset.UTC).toLocalDate()
                            selectedDueDisplay = selectedDate.format(displayFormatter)
                            selectedDueDateIso = "${selectedDate}T12:00:00Z"
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isEditMode) "Edit Task" else "Create Task") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = titleError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            if (titleError) {
                Text("Title is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            ExposedDropdownMenuBox(expanded = priorityExpanded, onExpandedChange = { priorityExpanded = it }) {
                OutlinedTextField(
                    value = selectedPriority,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                DropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    listOf("LOW", "MEDIUM", "HIGH").forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedPriority = item
                                priorityExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                OutlinedTextField(
                    value = selectedStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                DropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    listOf("TODO", "IN_PROGRESS", "COMPLETED").forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.replace('_', ' ')) },
                            onClick = {
                                selectedStatus = item
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = selectedDueDisplay,
                onValueChange = {},
                readOnly = true,
                label = { Text("Due Date (optional)") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        showDatePicker = true
                    }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Open date picker")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        focusManager.clearFocus()
                        showDatePicker = true
                    }
            )

            Text("Assign members", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = if (selectedMembers.isEmpty()) "Select team members" else "${selectedMembers.size} member(s) selected",
                onValueChange = {},
                readOnly = true,
                label = { Text("Members") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { assigneeExpanded = !assigneeExpanded }) {
                        Icon(Icons.Default.Person, contentDescription = "Toggle members")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { assigneeExpanded = true }
            )
            if (assigneeExpanded) {
                DropdownMenu(
                    expanded = assigneeExpanded,
                    onDismissRequest = { assigneeExpanded = false }
                ) {
                    if (usersLoading) {
                        DropdownMenuItem(text = { Text("Loading users...") }, onClick = {})
                    } else {
                        users.forEach { user ->
                            val isSelected = selectedMembers.any { it.id == user.id }
                            DropdownMenuItem(
                                text = { Text("${user.fullName} (${user.email})") },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                },
                                onClick = {
                                    val current = selectedMembers.indexOfFirst { it.id == user.id }
                                    if (current >= 0) {
                                        selectedMembers.removeAt(current)
                                    } else {
                                        selectedMembers.add(user)
                                    }
                                }
                            )
                        }
                    }
                }
            }
            if (assigneeError) {
                Text("Select at least one member", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            if (selectedMembers.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    selectedMembers.forEach { user ->
                        AssistChip(
                            onClick = {
                                selectedMembers.removeAll { it.id == user.id }
                            },
                            label = { Text(user.fullName) },
                            leadingIcon = { Icon(Icons.Default.Close, contentDescription = null) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    val request = if (isEditMode) {
                        UpdateTaskRequest(
                            title = title.trim(),
                            description = description.trim().ifBlank { null },
                            priority = selectedPriority,
                            status = selectedStatus,
                            dueDate = selectedDueDateIso,
                            assignedToIds = selectedMembers.map { it.id }
                        )
                    } else {
                        null
                    }
                    if (isFormValid) {
                        if (isEditMode && taskId != null && request != null) {
                            taskViewModel.updateTask(taskId, request)
                        } else {
                            taskViewModel.createTask(
                                CreateTaskRequest(
                                    title = title.trim(),
                                    description = description.trim().ifBlank { null },
                                    priority = selectedPriority,
                                    status = selectedStatus,
                                    dueDate = selectedDueDateIso,
                                    assignedToIds = selectedMembers.map { it.id }
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (isEditMode) "Update Task" else "Create Task")
                }
            }
        }
    }
}
