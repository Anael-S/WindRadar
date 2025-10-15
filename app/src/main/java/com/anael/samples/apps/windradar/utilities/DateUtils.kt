package com.anael.samples.apps.windradar.utilities

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val dateTimeFormats = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.getDefault()),
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    )

    /**
     * Parses an ISO-like date or datetime string into a LocalDateTime.
     * Defaults to midnight if time is missing.
     */
    private fun parseFlexibleDateTime(isoString: String): LocalDateTime {
        for (formatter in dateTimeFormats) {
            try {
                return when (formatter) {
                    dateTimeFormats[0] -> LocalDateTime.parse(isoString, formatter)
                    else -> LocalDate.parse(isoString, formatter).atStartOfDay()
                }
            } catch (_: Exception) {
            }
        }
        throw IllegalArgumentException("Invalid date format: $isoString")
    }

    /**
     * Converts ISO-like string into "Tuesday 2nd October"
     */
    fun formatToDayWithOrdinalAndMonth(isoString: String): String {
        return try {
            val date = parseFlexibleDateTime(isoString)

            val dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))
            val dayOfMonth = date.dayOfMonth
            val ordinal = getOrdinalSuffix(dayOfMonth)
            val month = date.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault()))

            "$dayOfWeek $dayOfMonth$ordinal $month"
        } catch (e: Exception) {
            isoString
        }
    }

    /**
     * Converts ISO-like string into "Tuesday 14:00"
     */
    fun formatToDayAndTime(isoString: String): String {
        return try {
            val date = parseFlexibleDateTime(isoString)

            val dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))
            val time = date.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))

            "$dayOfWeek $time"
        } catch (e: Exception) {
            isoString
        }
    }

    /**
     * Returns ordinal suffix for a given day number (1 → "st", 2 → "nd", etc.)
     */
    private fun getOrdinalSuffix(day: Int): String {
        return if (day in 11..13) "th" else when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

}
