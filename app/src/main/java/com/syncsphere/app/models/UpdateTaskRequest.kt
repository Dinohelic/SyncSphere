package com.syncsphere.app.models

data class UpdateTaskRequest(
    val title: String,
    val description: String?,
    val completed: Boolean
)