package com.syncsphere.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Dashboard : BottomNavItem(Routes.DASHBOARD, Icons.Default.Dashboard, "Dashboard")
    object Tasks : BottomNavItem(Routes.TASKS, Icons.Default.List, "Tasks")
    object Announcements : BottomNavItem(Routes.ANNOUNCEMENTS, Icons.Default.Campaign, "Announcements")
    object Events : BottomNavItem(Routes.EVENTS, Icons.Default.Event, "Events")
    object Profile : BottomNavItem(Routes.PROFILE, Icons.Default.Person, "Profile")
}

