package com.syncsphere.app.models

data class CreateTaskRequest(
    val title: String,
    val description: String?
)