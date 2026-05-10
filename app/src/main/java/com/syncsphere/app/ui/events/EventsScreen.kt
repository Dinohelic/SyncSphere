package com.syncsphere.app.ui.events

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.syncsphere.app.models.EventDto
import com.syncsphere.app.ui.components.EmptyState
import com.syncsphere.app.ui.common.formatDateTime
import com.syncsphere.app.ui.theme.Dimens
import com.syncsphere.app.utils.TokenManager
import com.syncsphere.app.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EventsScreen(
    eventViewModel: EventViewModel = hiltViewModel(),
    onAddEvent: (() -> Unit)? = null,
    onEditEvent: ((String) -> Unit)? = null
) {
    val events by eventViewModel.events.collectAsState()
    val isLoading by eventViewModel.isLoading.collectAsState()
    val mutationState by eventViewModel.mutationState.collectAsState()
    val deleteState by eventViewModel.deleteState.collectAsState()
    val errorMessage by eventViewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val isAdmin = remember { TokenManager.getUserRole(context).equals("ADMIN", ignoreCase = true) }
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDeleteEvent by remember { mutableStateOf<EventDto?>(null) }

    LaunchedEffect(Unit) {
        eventViewModel.getEvents()
    }

    LaunchedEffect(mutationState, deleteState, errorMessage) {
        when {
            deleteState?.isSuccess == true -> snackbarHostState.showSnackbar("Event deleted")
            mutationState?.isSuccess == true -> snackbarHostState.showSnackbar("Event saved")
            !errorMessage.isNullOrBlank() -> snackbarHostState.showSnackbar(errorMessage!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Events") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    text = { Text("Create") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    onClick = { onAddEvent?.invoke() }
                )
            }
        }
    ) { paddingValues ->
        Crossfade(targetState = isLoading, label = "event_loading_transition") { loading ->
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val eventList = events?.getOrNull().orEmpty().sortedBy { it.eventDate }
                if (eventList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyState(
                            title = "No upcoming moments",
                            subtitle = "Your team calendar is clear right now."
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(Dimens.spacing),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacing)
                    ) {
                        items(eventList, key = { it.id }) { event ->
                            EventCard(
                                event = event,
                                isAdmin = isAdmin,
                                onEditEvent = onEditEvent,
                                onRequestDelete = { pendingDeleteEvent = event }
                            )
                        }
                    }
                }
            }
        }
    }

    pendingDeleteEvent?.let { event ->
        AlertDialog(
            onDismissRequest = { pendingDeleteEvent = null },
            title = { Text("Delete event?") },
            text = { Text("This will remove ${event.title}.") },
            confirmButton = {
                TextButton(onClick = {
                    eventViewModel.deleteEvent(event.id)
                    pendingDeleteEvent = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteEvent = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventCard(
    event: EventDto,
    isAdmin: Boolean = false,
    onEditEvent: ((String) -> Unit)? = null,
    onRequestDelete: (() -> Unit)? = null
) {
    var actionMenuExpanded by remember { mutableStateOf(false) }

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
                .animateContentSize()
        ) {
            Column(modifier = Modifier.padding(Dimens.spacing)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(formatDateTime(event.eventDate)) },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) }
                    )
                }

                Spacer(modifier = Modifier.size(Dimens.spacing_sm))
                Text(text = event.title, style = MaterialTheme.typography.titleMedium)
                event.description?.let { desc ->
                    Spacer(modifier = Modifier.size(Dimens.spacing_xs))
                    Text(text = desc, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.size(Dimens.spacing_sm))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = event.venue, style = MaterialTheme.typography.bodySmall)
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
                    onEditEvent?.invoke(event.id)
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
