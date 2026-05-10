package com.syncsphere.app.ui.announcements

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.syncsphere.app.ui.components.EmptyState
import com.syncsphere.app.ui.common.formatDateTime
import com.syncsphere.app.ui.common.priorityColor
import com.syncsphere.app.ui.theme.Dimens
import com.syncsphere.app.utils.TokenManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.syncsphere.app.models.AnnouncementDto
import androidx.compose.ui.platform.LocalContext
import com.syncsphere.app.viewmodel.AnnouncementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    announcementViewModel: AnnouncementViewModel = hiltViewModel(),
    onAddAnnouncement: (() -> Unit)? = null
) {
    val announcements by announcementViewModel.announcements.collectAsState()
    val isLoading by announcementViewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val isAdmin = remember { TokenManager.getUserRole(context).equals("ADMIN", ignoreCase = true) }

    LaunchedEffect(Unit) {
        announcementViewModel.getAnnouncements()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Announcements") })
        },
        floatingActionButton = {
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    text = { Text("New") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    onClick = { onAddAnnouncement?.invoke() }
                )
            }
        }
    ) { paddingValues ->
        Crossfade(targetState = isLoading, label = "announcement_loading_transition") { loading ->
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val source = announcements?.getOrNull().orEmpty()
                val sorted = source.sortedWith(
                    compareByDescending<AnnouncementDto> { it.pinned }
                        .thenByDescending { it.createdAt }
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(Dimens.spacing),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)
                ) {
                    if (sorted.isEmpty()) {
                        item {
                            EmptyState(
                                title = "All quiet for now",
                                subtitle = "Team updates will appear here once shared."
                            )
                        }
                    } else {
                        items(
                            items = sorted,
                            key = { it.id }
                        ) { announcement ->
                            AnnouncementCard(announcement = announcement)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: AnnouncementDto) {
    val indicatorColor = priorityColor(announcement.priority)

    Card(
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
                Text(text = announcement.title, style = MaterialTheme.typography.titleMedium)
                if (announcement.pinned) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Pinned") },
                        leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spacing_xs))
            Text(text = announcement.message, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(Dimens.spacing_sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${announcement.createdBy.fullName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AssistChip(
                    onClick = {},
                    label = { Text(announcement.priority.uppercase()) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = indicatorColor.copy(alpha = 0.14f),
                        labelColor = indicatorColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spacing_xs))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = formatDateTime(announcement.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}