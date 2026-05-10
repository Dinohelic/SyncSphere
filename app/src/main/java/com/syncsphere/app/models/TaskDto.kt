package com.syncsphere.app.models

data class TaskDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val priority: String = "MEDIUM",
    val status: String = "PENDING",
    val dueDate: String? = null
)