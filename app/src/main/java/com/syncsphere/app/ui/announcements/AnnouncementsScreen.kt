package com.syncsphere.app.ui.announcements

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
import com.syncsphere.app.models.AnnouncementDto
import com.syncsphere.app.models.CreatedBy
import com.syncsphere.app.viewmodel.AnnouncementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(announcementViewModel: AnnouncementViewModel = hiltViewModel()) {
    val announcements by announcementViewModel.announcements.collectAsState()
    val isLoading by announcementViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        announcementViewModel.getAnnouncements()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Announcements") })
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            announcements?.getOrNull()?.let { announcementList ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    items(
                        items = announcementList,
                        key = { it.id }
                    ) { announcement ->
                        AnnouncementCard(announcement = announcement)
                    }
                }
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
            Text(text = announcement.title, style = MaterialTheme.typography.titleMedium)
            Text(text = announcement.message, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "By ${announcement.createdBy.fullName} on ${announcement.createdAt}", style = MaterialTheme.typography.bodySmall)
        }
    }
}