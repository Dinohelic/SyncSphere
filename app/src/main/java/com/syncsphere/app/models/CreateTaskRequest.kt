package com.syncsphere.app.models

data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val priority: String = "MEDIUM",
    val status: String = "TODO",
    val dueDate: String? = null,
    val assignedToIds: List<String>
)