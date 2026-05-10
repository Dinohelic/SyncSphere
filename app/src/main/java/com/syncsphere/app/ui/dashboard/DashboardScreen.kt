package com.syncsphere.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import com.syncsphere.app.ui.components.EmptyState
import com.syncsphere.app.ui.theme.Dimens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.syncsphere.app.ui.announcements.AnnouncementCard
import com.syncsphere.app.ui.events.EventCard
import com.syncsphere.app.ui.tasks.TaskCard
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
                val announcementList = announcements?.getOrNull().orEmpty()
                    .sortedWith(compareByDescending { it.pinned })
                    .take(3)
                val eventList = events?.getOrNull().orEmpty().sortedBy { it.eventDate }.take(3)
                val taskList = tasks?.getOrNull().orEmpty().take(4)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(Dimens.spacing),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacing)
                ) {
                    item {
                        GreetingCard()
                    }

                    item {
                        stats?.getOrNull()?.let { stat ->
                            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)) {
                                    DashboardStatCard(
                                        title = "Total",
                                        value = stat.totalTasks.toString(),
                                        icon = Icons.Default.ListAlt,
                                        color = Color(0xFF2563EB),
                                        modifier = Modifier.weight(1f)
                                    )
                                    DashboardStatCard(
                                        title = "Completed",
                                        value = stat.completedTasks.toString(),
                                        icon = Icons.Default.CheckCircle,
                                        color = Color(0xFF1B8A5A),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)) {
                                    DashboardStatCard(
                                        title = "Pending",
                                        value = stat.pendingTasks.toString(),
                                        icon = Icons.Default.Pending,
                                        color = Color(0xFFD97706),
                                        modifier = Modifier.weight(1f)
                                    )
                                    DashboardStatCard(
                                        title = "High Priority",
                                        value = stat.highPriorityTasks.toString(),
                                        icon = Icons.Default.Bolt,
                                        color = Color(0xFFDC2626),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        SectionTitle("Recent Tasks")
                    }

                    if (taskList.isEmpty()) {
                        item { EmptyState(title = "No active tasks", subtitle = "Tasks assigned to your team will show up here.") }
                    } else {
                        items(taskList, key = { it.id }) { task ->
                            TaskCard(task = task)
                        }
                    }

                    item { SectionTitle("Announcements") }

                    if (announcementList.isEmpty()) {
                        item {
                            EmptyState(title = "No announcements", subtitle = "Team updates are quiet at the moment.")
                        }
                    } else {
                        items(announcementList, key = { it.id }) { announcement ->
                            AnnouncementCard(announcement = announcement)
                        }
                    }

                    item { SectionTitle("Upcoming Events") }

                    if (eventList.isEmpty()) {
                        item { EmptyState(title = "No events scheduled", subtitle = "Upcoming milestones will appear here soon.") }
                    } else {
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
private fun GreetingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0EA5E9), Color(0xFF2563EB))
                    )
                )
                .padding(Dimens.spacing_lg)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacing_xs)) {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "SyncSphere Workspace",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = "Your team pulse for tasks, updates, and events.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun DashboardStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.card_elevation)
    ) {
        Column(modifier = Modifier.padding(Dimens.spacing)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, color = color)
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(Dimens.spacing_xs))
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(text = title, style = MaterialTheme.typography.headlineSmall)
}