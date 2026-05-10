package com.syncsphere.app.ui.dashboard

import com.syncsphere.app.ui.events.EventCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.syncsphere.app.models.AnnouncementDto
import com.syncsphere.app.models.EventDto
import com.syncsphere.app.models.TaskDto

import com.syncsphere.app.viewmodel.AnnouncementViewModel
import com.syncsphere.app.viewmodel.EventViewModel
import com.syncsphere.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    taskViewModel: TaskViewModel = hiltViewModel(),
    announcementViewModel: AnnouncementViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val stats by taskViewModel.dashboardStats.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()
    val announcements by announcementViewModel.announcements.collectAsState()
    val events by eventViewModel.events.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        taskViewModel.getDashboardStats()
        taskViewModel.getTasks()
        announcementViewModel.getAnnouncements()
        eventViewModel.getEvents()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                val announcementList = announcements?.getOrNull().orEmpty().take(3)
                val eventList = events?.getOrNull().orEmpty().take(3)
                val taskList = tasks?.getOrNull().orEmpty().take(3)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    item {
                        Column {
                            Text("Good Morning!", style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.height(16.dp))
                            stats?.getOrNull()?.let { stat ->
                                Row {
                                    StatCard(title = "Total Tasks", value = stat.totalTasks.toString(), modifier = Modifier.weight(1f))
                                    StatCard(title = "Completed", value = stat.completedTasks.toString(), modifier = Modifier.weight(1f))
                                }
                                Row {
                                    StatCard(title = "Pending", value = stat.pendingTasks.toString(), modifier = Modifier.weight(1f))
                                    StatCard(title = "High Priority", value = stat.highPriorityTasks.toString(), modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Recent Announcements", style = MaterialTheme.typography.headlineSmall)
                        }
                    }

                    items(announcementList) { announcement ->
                        AnnouncementCard(announcement = announcement)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Upcoming Events", style = MaterialTheme.typography.headlineSmall)
                    }

                    items(eventList) { event ->
                        EventCard(event = event)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Recent Tasks", style = MaterialTheme.typography.headlineSmall)
                    }

                    items(taskList) { task ->
                        TaskCard(task = task)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun TaskCard(task: TaskDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            task.description?.let { desc ->
                Text(text = desc, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Priority: ${task.priority}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Status: ${task.status}", style = MaterialTheme.typography.bodySmall)
            }
            task.dueDate?.let { dueDate ->
                Text(text = "Due: $dueDate", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: AnnouncementDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = announcement.message,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = announcement.createdAt,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}