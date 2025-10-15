package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the weather unit data from a weather search API
 */
data class DailyUnitsData(
    @field:SerializedName("temperature_2m_min") val temperatureUnit: String,
    @field:SerializedName("wind_speed_10m_max")  val windSpeedsUnit: String,
    @field:SerializedName("wind_gusts_10m_max") val windGustsUnit: String,
    @field:SerializedName("rain_sum") val rainSumUnit: String,
    @field:SerializedName("time") val time: String,
)

