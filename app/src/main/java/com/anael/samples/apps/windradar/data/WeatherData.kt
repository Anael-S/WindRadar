package com.anael.samples.apps.windradar.data

import com.anael.samples.apps.windradar.utilities.DateUtils
import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the weather data from a weather search API
 */
data class WeatherData(
    @field:SerializedName("wind_speed_10m") val windSpeeds: List<Double>,
    @field:SerializedName("wind_gusts_10m") val windGusts: List<Double>,
    @field:SerializedName("temperature_2m") val temperature: List<Double>,
    @field:SerializedName("time") private val _time: List<String>,
    @field:SerializedName("wind_direction_10m") val windDirection: List<Int>,
    @field:SerializedName("cloud_cover") val cloudCover: List<Int>,
) {
    val time: List<String>
        get() = _time.map { DateUtils.formatToEuropean(it) }
}

