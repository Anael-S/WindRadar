package com.anael.samples.apps.windradar.utilities.weather

import android.content.Context
import android.content.SharedPreferences
import com.anael.samples.apps.windradar.data.HourlyWeatherData
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object WeatherCalculator {


    private var initialized = false
    private lateinit var appContext: Context

    // Only runs once, automatically
    private fun ensureInitialized(context: Context) {
        if (!initialized) {
            appContext = context.applicationContext
            sharedPreferences = context.getSharedPreferences("WeatherAlerts", Context.MODE_PRIVATE)
            initialized = true
        }
    }


    private lateinit var sharedPreferences: SharedPreferences


    data class AlertResult(
        val shouldAlert: Boolean,
        val alertTime: String? = null,
        val windSpeed: String? = null,
        val hoursAboveThreshold: Int = 0  // longest consecutive hours above threshold within maxDayForward
    )

    private fun withinTimeWindow(h: Int, start: Int, end: Int): Boolean {
        // supports windows like 22 -> 6 (wrap across midnight)
        return if (start <= end) h in start..end else (h >= start || h <= end)
    }

    private fun withinDirWindow(dir: Int, start: Int, end: Int): Boolean {
        // supports windows like 300 -> 60 (wrap across 360/0)
        val s = (start % 361 + 361) % 361
        val e = (end % 361 + 361) % 361
        return if (s <= e) dir in s..e else (dir >= s || dir <= e)
    }

    fun calculateIfAlertIsNecessary(
        context: Context,
        hourlyWeatherData: HourlyWeatherData,
        windThreshold: Float,                 // min wind
        gustThreshold: Float,                 // min gust
        startHour: Int,                       // 0..23
        endHour: Int,                         // 0..23  (supports wrap-around  e.g. 22->6)
        maxDayForward: Int = 1,
        directions: List<Int>? = null,        // OPTIONAL: degrees 0..360 per hour (same indexing as times)
        dirStart: Int? = null,
        dirEnd: Int? = null
    ): AlertResult {
        ensureInitialized(context)

        val timestamps = hourlyWeatherData.rawTime
        val windSpeeds = hourlyWeatherData.windSpeeds
        val gustSpeeds = hourlyWeatherData.windGusts

        val alertedDays = getAlertedDays(context)

        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale("nl", "NL"))
        val dateKeyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val zone = ZoneId.of("Europe/Amsterdam")
        val now = LocalDateTime.now(zone)
        val cutoffDate = now.plusDays(maxDayForward.toLong())

        var currentStreak = 0
        var maxStreak = 0

        // 1) Compute longest streak but ONLY inside the future window and inside the selected time window (+ optional direction)
        for (i in timestamps.indices) {
            val timeStr = timestamps[i]
            val wind = windSpeeds.getOrNull(i) ?: continue
            val gust = gustSpeeds.getOrNull(i) ?: continue

            val t = try { LocalDateTime.parse(timeStr, inputFormatter) } catch (_: Exception) { continue }

            if (t.isBefore(now)) { currentStreak = 0; continue }
            if (t.isAfter(cutoffDate)) break

            val hour = t.hour
            if (!withinTimeWindow(hour, startHour, endHour)) { currentStreak = 0; continue }

            // Optional direction filter if provided + bounds set
            if (directions != null && dirStart != null && dirEnd != null) {
                val dir = directions.getOrNull(i)
                if (dir == null || !withinDirWindow(dir, dirStart, dirEnd)) {
                    currentStreak = 0
                    continue
                }
            }

            val isAbove = (wind >= windThreshold) || (gust >= gustThreshold)
            if (isAbove) {
                currentStreak++
                if (currentStreak > maxStreak) maxStreak = currentStreak
            } else {
                currentStreak = 0
            }
        }

        // 2) Find first alert occurrence in the selected window (future only), not already alerted for that day
        for (i in timestamps.indices) {
            val timeStr = timestamps[i]
            val wind = windSpeeds.getOrNull(i) ?: continue
            val gust = gustSpeeds.getOrNull(i) ?: continue

            val t = try { LocalDateTime.parse(timeStr, inputFormatter) } catch (_: Exception) { continue }

            if (t.isBefore(now)) continue
            if (t.isAfter(cutoffDate)) break

            val hour = t.hour
            if (!withinTimeWindow(hour, startHour, endHour)) continue

            if (directions != null && dirStart != null && dirEnd != null) {
                val dir = directions.getOrNull(i) ?: continue
                if (!withinDirWindow(dir, dirStart, dirEnd)) continue
            }

            val isAlert = (wind >= windThreshold) || (gust >= gustThreshold)
            if (isAlert) {
                val dayKey = t.format(dateKeyFormatter)
                if (!alertedDays.contains(dayKey)) {
                    saveAlertForDay(context, dayKey)
                    val readableTime = t.format(outputFormatter)
                    val windStr = if (gust >= gustThreshold) gust.toString() else wind.toString()
                    return AlertResult(
                        shouldAlert = true,
                        alertTime = readableTime,
                        windSpeed = windStr,
                        hoursAboveThreshold = maxStreak
                    )
                }
            }
        }

        return AlertResult(false, null, null, maxStreak)
    }

    private fun getAlertedDays(context: Context): Set<String> {
        ensureInitialized(context)
        val jsonString = sharedPreferences.getString("alerted_days", "{}") ?: "{}"
        val jsonObject = JSONObject(jsonString)
        return jsonObject.keys().asSequence().toSet()
    }

    private fun saveAlertForDay(context: Context, day: String) {
        ensureInitialized(context)
        val jsonString = sharedPreferences.getString("alerted_days", "{}") ?: "{}"
        val jsonObject = JSONObject(jsonString)
        jsonObject.put(day, true)
        sharedPreferences.edit().putString("alerted_days", jsonObject.toString()).apply()
    }

    fun clearAlerts(context: Context) {
        ensureInitialized(context)
        sharedPreferences.edit().remove("alerted_days").apply()
    }
}
