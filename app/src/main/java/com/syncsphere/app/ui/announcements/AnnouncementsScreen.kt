package com.syncsphere.app.ui.announcements

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import com.syncsphere.app.models.AnnouncementDto
import com.syncsphere.app.ui.components.EmptyState
import com.syncsphere.app.ui.common.formatDateTime
import com.syncsphere.app.ui.common.priorityColor
import com.syncsphere.app.ui.theme.Dimens
import com.syncsphere.app.utils.TokenManager
import com.syncsphere.app.viewmodel.AnnouncementViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AnnouncementsScreen(
    announcementViewModel: AnnouncementViewModel = hiltViewModel(),
    onAddAnnouncement: (() -> Unit)? = null,
    onEditAnnouncement: ((String) -> Unit)? = null
) {
    val announcements by announcementViewModel.announcements.collectAsState()
    val isLoading by announcementViewModel.isLoading.collectAsState()
    val mutationState by announcementViewModel.mutationState.collectAsState()
    val deleteState by announcementViewModel.deleteState.collectAsState()
    val errorMessage by announcementViewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val isAdmin = remember { TokenManager.getUserRole(context).equals("ADMIN", ignoreCase = true) }
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDeleteAnnouncement by remember { mutableStateOf<AnnouncementDto?>(null) }

    LaunchedEffect(Unit) {
        announcementViewModel.getAnnouncements()
    }

    LaunchedEffect(mutationState, deleteState, errorMessage) {
        when {
            deleteState?.isSuccess == true -> snackbarHostState.showSnackbar("Announcement deleted")
            mutationState?.isSuccess == true -> snackbarHostState.showSnackbar("Announcement saved")
            !errorMessage.isNullOrBlank() -> snackbarHostState.showSnackbar(errorMessage!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Announcements") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        items(items = sorted, key = { it.id }) { announcement ->
                            AnnouncementCard(
                                announcement = announcement,
                                isAdmin = isAdmin,
                                onEditAnnouncement = onEditAnnouncement,
                                onRequestDelete = { pendingDeleteAnnouncement = announcement }
                            )
                        }
                    }
                }
            }
        }
    }

    pendingDeleteAnnouncement?.let { announcement ->
        AlertDialog(
            onDismissRequest = { pendingDeleteAnnouncement = null },
            title = { Text("Delete announcement?") },
            text = { Text("This will remove ${announcement.title}.") },
            confirmButton = {
                TextButton(onClick = {
                    announcementViewModel.deleteAnnouncement(announcement.id)
                    pendingDeleteAnnouncement = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteAnnouncement = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnnouncementCard(
    announcement: AnnouncementDto,
    isAdmin: Boolean = false,
    onEditAnnouncement: ((String) -> Unit)? = null,
    onRequestDelete: (() -> Unit)? = null
) {
    val priorityText = (announcement.priority ?: "NORMAL").uppercase()
    val indicatorColor = priorityColor(priorityText)
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

                Spacer(modifier = Modifier.size(Dimens.spacing_xs))
                Text(text = announcement.message, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.size(Dimens.spacing_sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "By ${announcement.createdBy?.fullName ?: "Unknown"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(priorityText) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = indicatorColor.copy(alpha = 0.14f),
                            labelColor = indicatorColor
                        )
                    )
                }

                Spacer(modifier = Modifier.size(Dimens.spacing_xs))
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

        DropdownMenu(
            expanded = actionMenuExpanded,
            onDismissRequest = { actionMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                onClick = {
                    actionMenuExpanded = false
                    onEditAnnouncement?.invoke(announcement.id)
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
