package com.syncsphere.app.models

data class DashboardStatsResponse(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val highPriorityTasks: Int = 0
)