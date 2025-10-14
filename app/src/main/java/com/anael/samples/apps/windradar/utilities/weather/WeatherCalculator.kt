/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anael.samples.apps.windradar.utilities.weather

import android.content.Context
import android.content.SharedPreferences
import com.anael.samples.apps.windradar.data.WindData
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
            // Your existing init() code here, e.g. load prefs, set up cache
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

    fun calculateIfAlertIsNecessary(
        context: Context,
        windData: WindData,
        windThreshold: Int = 40,
        gustThreshold: Int = 40,
        maxDayForward: Int = 1
    ): AlertResult {
        ensureInitialized(context)
        val timestamps = windData.time
        val windSpeeds = windData.windSpeeds
        val gustSpeeds = windData.windGusts

        val alertedDays = getAlertedDays(context)

        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale("nl", "NL"))
        val dateKeyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val now = LocalDateTime.now(ZoneId.of("Europe/Amsterdam"))
        val cutoffDate = now.plusDays(maxDayForward.toLong())

        var currentStreak = 0
        var maxStreak = 0

        // Count longest streak only within maxDayForward window
        for (i in timestamps.indices) {
            val timeStr = timestamps[i]
            val windSpeed = windSpeeds.getOrNull(i) ?: continue
            val gustSpeed = gustSpeeds.getOrNull(i) ?: continue

            val alertTime = try {
                LocalDateTime.parse(timeStr, inputFormatter)
            } catch (e: Exception) {
                continue
            }

            if (alertTime.isBefore(now)) {
                currentStreak = 0
                continue
            }
            if (alertTime.isAfter(cutoffDate)) {
                // Stop processing timestamps beyond cutoffDate
                break
            }

            val isAboveThreshold = (windSpeed > windThreshold) || (gustSpeed > gustThreshold)
            if (isAboveThreshold) {
                currentStreak++
                if (currentStreak > maxStreak) {
                    maxStreak = currentStreak
                }
            } else {
                currentStreak = 0
            }
        }

        // Find first alert in window to trigger alert and save it
        for (i in timestamps.indices) {
            val timeStr = timestamps[i]
            val windSpeed = windSpeeds.getOrNull(i) ?: continue
            val gustSpeed = gustSpeeds.getOrNull(i) ?: continue

            val alertTime = try {
                LocalDateTime.parse(timeStr, inputFormatter)
            } catch (e: Exception) {
                continue
            }

            val dayKey = alertTime.format(dateKeyFormatter)

            val isAlert = (windSpeed > windThreshold) || (gustSpeed > gustThreshold)

            if (
                isAlert &&
                alertTime.isAfter(now) &&
                alertTime.isBefore(cutoffDate) &&
                !alertedDays.contains(dayKey)
            ) {
                saveAlertForDay(context, dayKey)
                val readableTime = alertTime.format(outputFormatter)
                val windSpeedStr = if (gustSpeed > gustThreshold) gustSpeed.toString() else windSpeed.toString()
                return AlertResult(true, readableTime, windSpeedStr, maxStreak)
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
