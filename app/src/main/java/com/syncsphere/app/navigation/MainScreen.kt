package com.syncsphere.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.syncsphere.app.ui.announcements.AnnouncementsScreen
import com.syncsphere.app.ui.dashboard.DashboardScreen
import com.syncsphere.app.ui.events.EventsScreen
import com.syncsphere.app.ui.profile.ProfileScreen
import com.syncsphere.app.ui.tasks.TasksScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val items = listOf(
                BottomNavItem.Dashboard,
                BottomNavItem.Tasks,
                BottomNavItem.Announcements,
                BottomNavItem.Events,
                BottomNavItem.Profile
            )
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Routes.DASHBOARD,
            Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) { DashboardScreen() }
            composable(Routes.TASKS) { TasksScreen() }
            composable(Routes.ANNOUNCEMENTS) { AnnouncementsScreen() }
            composable(Routes.EVENTS) { EventsScreen() }
            composable(Routes.PROFILE) { ProfileScreen(navController = navController) }
        }
    }
}

