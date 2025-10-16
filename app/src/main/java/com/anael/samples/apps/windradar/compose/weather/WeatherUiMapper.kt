package com.anael.samples.apps.windradar.compose.weather

import com.anael.samples.apps.windradar.compose.weather.model.DailyUiItem
import com.anael.samples.apps.windradar.compose.weather.model.HourlyUiItem
import com.anael.samples.apps.windradar.data.DailyWeatherWithUnitData
import com.anael.samples.apps.windradar.data.HourlyWeatherWithUnitData
import com.anael.samples.apps.windradar.utilities.DateUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class WeatherUiMapper @Inject constructor() {

    fun mapDaily(data: DailyWeatherWithUnitData): List<DailyUiItem> {
        val d = data.dailyWeatherData
        val u = data.dailyUnits

        return d.time.indices.map { i ->
            val brightness = sunshineToBrightnessFactor(
                sunshineSeconds = d.sunshineDuration[i],
                daylightSeconds = d.daylightDuration[i]
            )

            DailyUiItem(
                time = DateUtils.formatToDayWithOrdinalAndMonth(d.time[i]),
                tempMinText = valueWithUnit(d.temperatureMin[i], u.temperatureUnit, 0),
                tempUnit = u.temperatureUnit,
                tempMaxText = valueWithUnit(d.temperatureMax[i], u.temperatureUnit, 0),
                windSpeedText = valueWithoutUnit(d.windSpeeds[i], 1),
                windGustText = valueWithoutUnit(d.windGusts[i], 1),
                windUnit = u.windSpeedsUnit,
                rainText = valueWithoutUnit(d.rainSum[i], 1),
                rainUnit = u.rainSumUnit,
                brightnessFactor = brightness,
                emoji = pickEmojiForBrightness(brightness)
            )
        }
    }

    fun mapHourly(data: HourlyWeatherWithUnitData): List<HourlyUiItem> {
        val h = data.hourlyWeatherData
        val u = data.hourlyUnits

        val isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

        return h.rawTime.indices.map { i ->
            val iso = h.rawTime[i]

            // UI-friendly time, e.g. "Wednesday 14:00"
            val timeText = DateUtils.formatToDayAndTime(iso)

            // Daylight/brightness from hour-of-day (0..1). Be lenient on parse errors.
            val brightness = runCatching {
                val ldt = LocalDateTime.parse(iso, isoFormatter)
                hourToDaylight(ldt.hour, ldt.minute)
            }.getOrElse { 1f }

            val emoji = pickEmojiForBrightness(brightness)

            HourlyUiItem(
                time = timeText,
                tempText = valueWithUnit(h.temperature[i], u.temperatureUnit, 1),
                tempUnit = u.temperatureUnit,
                windText = valueWithoutUnit(h.windSpeeds[i], 1),
                gustText = valueWithoutUnit(h.windGusts[i], 1),
                cloudsText = valueWithoutUnit(h.cloudCover[i]),
                cloudUnit = u.cloudCoverUnits,
                windUnit = u.windSpeedsUnit,
                windDirectionDeg = h.windDirection[i],
                brightnessFactor = brightness,
                emoji = emoji
            )
        }
    }

    private fun pickEmojiForBrightness(brightness: Float): String = when {
        brightness < 0.15f -> "☁️"
        brightness < 0.35f -> "🌥️"
        brightness < 0.55f -> "🌥"
        brightness < 0.75f -> "🌤️"
        else -> "🌞"
    }
}
