package com.anael.samples.apps.windradar.data

import com.anael.samples.apps.windradar.utilities.DateUtils
import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the weather data from a weather search API
 */
data class DailyWeatherData(
    @field:SerializedName("time") private val _time: List<String>,
    @field:SerializedName("temperature_2m_min") val temperatureMin: List<Double>,
    @field:SerializedName("temperature_2m_max") val temperatureMax: List<Double>,
    @field:SerializedName("wind_speed_10m_max") val windSpeeds: List<Double>,
    @field:SerializedName("wind_gusts_10m_max") val windGusts: List<Double>,
    @field:SerializedName("rain_sum") val rainSum: List<Double>,
) {
    val time: List<String>
        get() = _time.map { DateUtils.formatToDayWithOrdinalAndMonth(it) }
}

