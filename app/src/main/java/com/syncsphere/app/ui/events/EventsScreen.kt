package com.syncsphere.app.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.syncsphere.app.models.EventDto
import com.syncsphere.app.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(eventViewModel: EventViewModel = hiltViewModel()) {
    val events by eventViewModel.events.collectAsState()
    val isLoading by eventViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        eventViewModel.getEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Events") })
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            events?.getOrNull().isNullOrEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No events yet")
                }
            }
            else -> {
                val eventList = events!!.getOrNull().orEmpty()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    items(eventList) { event ->
                        EventCard(event = event)
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
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
            event.description?.let { desc ->
                Text(text = desc, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "At ${event.venue} on ${event.eventDate}", style = MaterialTheme.typography.bodySmall)
        }
    }
}