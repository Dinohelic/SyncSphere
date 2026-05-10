package com.syncsphere.app.models

data class AnnouncementDto(
    val id: String,
    val title: String,
    val message: String,
    val pinned: Boolean,
    val createdAt: String,
    val createdBy: CreatedBy,
    val priority: String = "NORMAL"
)

data class CreatedBy(
    val fullName: String
)

data class EventDto(
    val id: String,
    val title: String,
    val venue: String,
    val description: String?,
    val eventDate: String
)

data class CreateAnnouncementRequest(
    val title: String,
    val message: String,
    val pinned: Boolean
)

data class CreateEventRequest(
    val title: String,
    val venue: String,
    val description: String?,
    val eventDate: String
)
