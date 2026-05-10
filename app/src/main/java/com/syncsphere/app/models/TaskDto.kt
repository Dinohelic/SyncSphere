package com.syncsphere.app.models

data class TaskDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val priority: String = "MEDIUM",
    val status: String = "TODO",
    val dueDate: String? = null,
    val assignedUser: String? = null,
    val assignedTo: AssignedToDto? = null,
    val assignedToId: String? = null,
    val assignedMembers: List<AssignedMemberDto> = emptyList()
)

data class AssignedToDto(
    val id: String? = null,
    val fullName: String? = null,
    val email: String? = null
)

data class AssignedMemberDto(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String
)