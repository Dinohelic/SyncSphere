package com.syncsphere.app.ui.common

import com.syncsphere.app.models.AnnouncementDto
import com.syncsphere.app.models.CreatedBy
import com.syncsphere.app.models.DashboardStatsResponse
import com.syncsphere.app.models.EventDto
import com.syncsphere.app.models.TaskDto

object DemoSeedData {

    val tasks = listOf(
        TaskDto(
            id = "task-1",
            title = "Finalize Q2 Roadmap",
            description = "Align product priorities with engineering scope for Q2.",
            priority = "HIGH",
            status = "IN_PROGRESS",
            dueDate = "2026-05-14T10:00:00.000Z",
            assignedUser = "Ariana Lee"
        ),
        TaskDto(
            id = "task-2",
            title = "Fix onboarding crash on Android 14",
            description = "Resolve rare crash during permission flow.",
            priority = "HIGH",
            status = "TODO",
            dueDate = "2026-05-12T16:30:00.000Z",
            assignedUser = "Rohan Patel"
        ),
        TaskDto(
            id = "task-3",
            title = "Prepare investor metrics deck",
            description = "Add activation and retention charts for weekly review.",
            priority = "MEDIUM",
            status = "IN_PROGRESS",
            dueDate = "2026-05-18T09:00:00.000Z",
            assignedUser = "Maya Chen"
        ),
        TaskDto(
            id = "task-4",
            title = "Design system icon audit",
            description = "Replace legacy icon set with cohesive rounded icons.",
            priority = "LOW",
            status = "COMPLETED",
            dueDate = "2026-05-10T08:00:00.000Z",
            assignedUser = "Noah Kim"
        ),
        TaskDto(
            id = "task-5",
            title = "Publish release notes v1.3",
            description = "Draft changelog and coordinate with support team.",
            priority = "MEDIUM",
            status = "TODO",
            dueDate = "2026-05-16T12:00:00.000Z",
            assignedUser = "Sarah Ali"
        ),
        TaskDto(
            id = "task-6",
            title = "Automate smoke test suite",
            description = "Run nightly integration checks across API endpoints.",
            priority = "HIGH",
            status = "IN_PROGRESS",
            dueDate = "2026-05-20T11:00:00.000Z",
            assignedUser = "Ethan Clark"
        ),
        TaskDto(
            id = "task-7",
            title = "Refine profile settings UX",
            description = "Improve spacing and hierarchy for account controls.",
            priority = "LOW",
            status = "COMPLETED",
            dueDate = "2026-05-09T15:00:00.000Z",
            assignedUser = "Ariana Lee"
        ),
        TaskDto(
            id = "task-8",
            title = "Migrate analytics events naming",
            description = "Standardize events to match BI naming conventions.",
            priority = "MEDIUM",
            status = "TODO",
            dueDate = "2026-05-22T14:00:00.000Z",
            assignedUser = "Rohan Patel"
        )
    )

    val announcements = listOf(
        AnnouncementDto(
            id = "ann-1",
            title = "Platform Stability Push",
            message = "This week we are focusing on crash-free sessions and startup performance.",
            pinned = true,
            createdAt = "2026-05-10T08:15:00.000Z",
            createdBy = CreatedBy(fullName = "Priya Nair"),
            priority = "HIGH"
        ),
        AnnouncementDto(
            id = "ann-2",
            title = "All Hands on Friday",
            message = "Quarterly all-hands starts at 5:00 PM in the town hall room.",
            pinned = true,
            createdAt = "2026-05-09T10:00:00.000Z",
            createdBy = CreatedBy(fullName = "Ethan Clark"),
            priority = "MEDIUM"
        ),
        AnnouncementDto(
            id = "ann-3",
            title = "Design Critique Session",
            message = "Upload your latest flows before Wednesday noon for review.",
            pinned = false,
            createdAt = "2026-05-08T13:40:00.000Z",
            createdBy = CreatedBy(fullName = "Maya Chen"),
            priority = "LOW"
        ),
        AnnouncementDto(
            id = "ann-4",
            title = "Security Review Checklist",
            message = "Use the updated checklist before merging auth-related pull requests.",
            pinned = false,
            createdAt = "2026-05-07T11:25:00.000Z",
            createdBy = CreatedBy(fullName = "Noah Kim"),
            priority = "HIGH"
        ),
        AnnouncementDto(
            id = "ann-5",
            title = "Hiring Pipeline Update",
            message = "We are scheduling final interviews for two senior Android candidates.",
            pinned = false,
            createdAt = "2026-05-06T09:10:00.000Z",
            createdBy = CreatedBy(fullName = "Sarah Ali"),
            priority = "MEDIUM"
        )
    )

    val events = listOf(
        EventDto(
            id = "evt-1",
            title = "Sprint Planning",
            venue = "Studio A",
            description = "Plan deliverables and dependencies for Sprint 19.",
            eventDate = "2026-05-13T09:30:00.000Z"
        ),
        EventDto(
            id = "evt-2",
            title = "Customer Feedback Roundtable",
            venue = "Zoom - Product Research",
            description = "Review customer pain points from support calls.",
            eventDate = "2026-05-15T17:00:00.000Z"
        ),
        EventDto(
            id = "evt-3",
            title = "Architecture Office Hours",
            venue = "War Room",
            description = "Open review for state management and API contracts.",
            eventDate = "2026-05-19T14:00:00.000Z"
        ),
        EventDto(
            id = "evt-4",
            title = "Release Readiness Check",
            venue = "Command Center",
            description = "Go/no-go meeting for v1.3 rollout.",
            eventDate = "2026-05-23T11:00:00.000Z"
        )
    )

    fun dashboardStats(tasks: List<TaskDto>): DashboardStatsResponse {
        val normalized = tasks.map { it.status.uppercase() }
        val completed = normalized.count { it == "COMPLETED" }
        val pending = normalized.count { it == "TODO" || it == "PENDING" || it == "IN_PROGRESS" }
        val highPriority = tasks.count { it.priority.equals("HIGH", ignoreCase = true) }
        return DashboardStatsResponse(
            totalTasks = tasks.size,
            completedTasks = completed,
            pendingTasks = pending,
            highPriorityTasks = highPriority
        )
    }
}
