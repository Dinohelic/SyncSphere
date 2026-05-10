package com.syncsphere.app.ui.common

import androidx.compose.ui.graphics.Color
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val cardDateFormatter = DateTimeFormatter.ofPattern("dd MMM, hh:mm a", Locale.getDefault())
private val compactDateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())

fun formatDateTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "Not scheduled"
    return try {
        OffsetDateTime.parse(raw).format(cardDateFormatter)
    } catch (_: Exception) {
        raw.take(16)
    }
}

fun formatCompactDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "No due date"
    return try {
        OffsetDateTime.parse(raw).format(compactDateFormatter)
    } catch (_: Exception) {
        raw.take(10)
    }
}

fun statusColor(status: String): Color = when (status.uppercase()) {
    "COMPLETED" -> Color(0xFF1B8A5A)
    "IN_PROGRESS" -> Color(0xFFD97706)
    else -> Color(0xFF6B7280)
}

fun priorityColor(priority: String): Color = when (priority.uppercase()) {
    "HIGH" -> Color(0xFFDC2626)
    "LOW" -> Color(0xFF2563EB)
    else -> Color(0xFF7C3AED)
}
