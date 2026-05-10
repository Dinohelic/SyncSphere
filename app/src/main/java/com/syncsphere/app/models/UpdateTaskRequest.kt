package com.syncsphere.app.models

data class UpdateTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val priority: String? = null,
    val status: String? = null,
    val dueDate: String? = null,
    val assignedToIds: List<String>? = null
)