package com.syncsphere.app.ui.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.syncsphere.app.models.CreateEventRequest
import com.syncsphere.app.viewmodel.EventViewModel
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    eventId: String? = null,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateDisplay by remember { mutableStateOf("") }
    var eventDateIso by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isLoading by eventViewModel.isLoading.collectAsState()
    val createState by eventViewModel.createEventState.collectAsState()
    val errorMessage by eventViewModel.errorMessage.collectAsState()
    val events by eventViewModel.events.collectAsState()

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isEditMode = !eventId.isNullOrBlank()
    var hasPrefilled by remember(eventId) { mutableStateOf(false) }

    val currentEvent = events?.getOrNull()?.firstOrNull { it.id == eventId }

    val titleError = title.isBlank()
    val venueError = venue.isBlank()
    val dateError = eventDateIso.isNullOrBlank()
    val isValid = !titleError && !venueError && !dateError

    val datePickerState = rememberDatePickerState()
    val displayFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            eventViewModel.clearOperationStates()
        }
    }

    LaunchedEffect(Unit) {
        if (isEditMode) {
            eventViewModel.getEvents()
        }
    }

    LaunchedEffect(currentEvent?.id) {
        val event = currentEvent ?: return@LaunchedEffect
        if (!hasPrefilled) {
            title = event.title
            venue = event.venue
            description = event.description.orEmpty()
            eventDateIso = event.eventDate
            dateDisplay = try {
                java.time.OffsetDateTime.parse(event.eventDate).format(displayFormatter)
            } catch (_: Exception) {
                event.eventDate.take(10)
            }
            hasPrefilled = true
        }
    }

    LaunchedEffect(createState) {
        createState?.onSuccess {
            snackbarHostState.showSnackbar(if (isEditMode) "Event updated" else "Event created")
            eventViewModel.clearOperationStates()
            eventViewModel.getEvents()
            navController.popBackStack()
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
                            dateDisplay = selectedDate.format(displayFormatter)
                            eventDateIso = "${selectedDate}T12:00:00Z"
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
        topBar = { TopAppBar(title = { Text("Create Event") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                singleLine = true,
                isError = titleError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) })
            )
            if (titleError) {
                Text("Title is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = venue,
                onValueChange = { venue = it },
                label = { Text("Venue") },
                singleLine = true,
                isError = venueError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) })
            )
            if (venueError) {
                Text("Venue is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
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
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) })
            )

            OutlinedTextField(
                value = dateDisplay,
                onValueChange = {},
                readOnly = true,
                label = { Text("Event Date") },
                isError = dateError,
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
            if (dateError) {
                Text("Event date is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (isValid) {
                        val request = CreateEventRequest(
                            title = title.trim(),
                            venue = venue.trim(),
                            description = description.trim().ifBlank { null },
                            eventDate = eventDateIso!!
                        )
                        if (isEditMode && eventId != null) {
                            eventViewModel.updateEvent(eventId, request)
                        } else {
                            eventViewModel.createEvent(request)
                        }
                    }
                },
                enabled = isValid && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Text(if (isEditMode) "Update Event" else "Create Event")
                }
            }
        }
    }
}
