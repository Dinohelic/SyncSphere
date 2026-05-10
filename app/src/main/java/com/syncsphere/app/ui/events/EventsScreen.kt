package com.syncsphere.app.ui.events

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.syncsphere.app.models.EventDto
import com.syncsphere.app.viewmodel.EventViewModel
import com.syncsphere.app.ui.components.EmptyState
import com.syncsphere.app.ui.common.formatDateTime
import com.syncsphere.app.ui.theme.Dimens
import com.syncsphere.app.utils.TokenManager
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    eventViewModel: EventViewModel = hiltViewModel(),
    onAddEvent: (() -> Unit)? = null
) {
    val events by eventViewModel.events.collectAsState()
    val isLoading by eventViewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val isAdmin = remember { TokenManager.getUserRole(context).equals("ADMIN", ignoreCase = true) }

    LaunchedEffect(Unit) {
        eventViewModel.getEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Events") })
        },
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
                            EventCard(event = event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(Dimens.spacing)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Dimens.spacing_sm, vertical = Dimens.spacing_xs),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(text = formatDateTime(event.eventDate), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
            event.description?.let { desc ->
                Spacer(modifier = Modifier.height(Dimens.spacing_xs))
                Text(text = desc, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = event.venue, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}