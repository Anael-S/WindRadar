package com.anael.samples.apps.windradar.domain

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Filters hourly data so that only entries at or after the current hour remain.
 * e.g. at 08:45 -> keep 08:00, 09:00, ...; drop 00:00..07:00.
 *
 * @param nowClock inject for testability; use Clock.systemDefaultZone() in prod.
 * @param inputFormatter matches your API hourly time strings (e.g. "yyyy-MM-dd'T'HH:mm")
 * @param zone the timezone that the API timestamps relate to (API tz or device tz).
 */
class FilterUpcomingHourly(
    private val nowClock: Clock = Clock.systemDefaultZone(),
    private val inputFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
) {
    operator fun <T> invoke(
        items: List<T>,
        zone: ZoneId,
        timeSelector: (T) -> String
    ): List<T> {
        val nowZdt = ZonedDateTime.now(nowClock).withZoneSameInstant(zone)
        val startOfCurrentHour = nowZdt.truncatedTo(ChronoUnit.HOURS)

        return items.filter { item ->
            val iso = timeSelector(item)
            runCatching {
                val ldt = LocalDateTime.parse(iso, inputFormatter)
                val itemZdt = ldt.atZone(zone)
                !itemZdt.isBefore(startOfCurrentHour) // keep current hour and future
            }.getOrElse { true } // on parse error, keep the item (safer for UI)
        }
    }
}
