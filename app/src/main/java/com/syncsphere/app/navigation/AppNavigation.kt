package com.syncsphere.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.syncsphere.app.ui.auth.LoginScreen
import com.syncsphere.app.ui.auth.RegisterScreen
import com.syncsphere.app.ui.auth.SplashScreen
import com.syncsphere.app.ui.announcements.CreateAnnouncementScreen
import com.syncsphere.app.ui.events.CreateEventScreen
import com.syncsphere.app.ui.tasks.TaskFormScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(Routes.MAIN) {
            MainScreen(mainNavController = navController)
        }
        composable(Routes.TASK_FORM) {
            TaskFormScreen(navController)
        }
        composable(
            Routes.TASK_FORM_EDIT,
            arguments = listOf(navArgument("taskId") { defaultValue = "" })
        ) { backStackEntry ->
            TaskFormScreen(
                navController = navController,
                taskId = backStackEntry.arguments?.getString("taskId")
            )
        }
        composable(Routes.ANNOUNCEMENT_FORM) {
            CreateAnnouncementScreen(navController)
        }
        composable(
            Routes.ANNOUNCEMENT_FORM_EDIT,
            arguments = listOf(navArgument("announcementId") { defaultValue = "" })
        ) { backStackEntry ->
            CreateAnnouncementScreen(
                navController = navController,
                announcementId = backStackEntry.arguments?.getString("announcementId")
            )
        }
        composable(Routes.EVENT_FORM) {
            CreateEventScreen(navController)
        }
        composable(
            Routes.EVENT_FORM_EDIT,
            arguments = listOf(navArgument("eventId") { defaultValue = "" })
        ) { backStackEntry ->
            CreateEventScreen(
                navController = navController,
                eventId = backStackEntry.arguments?.getString("eventId")
            )
        }
    }
}
