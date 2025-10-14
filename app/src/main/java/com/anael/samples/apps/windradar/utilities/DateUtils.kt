package com.anael.samples.apps.windradar.utilities

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    /**
     * Converts an ISO8601 datetime string (e.g. "2025-10-14T19:00")
     * into a European-style format (e.g. "14/10/2025 19:00").
     */
    fun formatToEuropean(isoString: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            val date = LocalDateTime.parse(isoString, inputFormatter)

            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
            date.format(outputFormatter)
        } catch (e: Exception) {
            isoString // fallback in case parsing fails
        }
    }
}
