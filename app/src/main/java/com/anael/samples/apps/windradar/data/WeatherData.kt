package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the weather data from a weather search API
 */
data class WeatherData(
    @field:SerializedName("wind_speed_10m")  val windSpeeds: List<Double>,
    @field:SerializedName("wind_gusts_10m") val windGusts: List<Double>,
    @field:SerializedName("temperature_2m") val temperature: List<Double>,
    @field:SerializedName("time") val time: List<String>
)

