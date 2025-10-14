package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the weather unit data from a weather search API
 */
data class HourlyUnitsData(
    @field:SerializedName("wind_speed_10m")  val windSpeedsUnit: String,
    @field:SerializedName("wind_gusts_10m") val windGustsUnit: String,
    @field:SerializedName("temperature_2m") val temperatureUnit: String,
    @field:SerializedName("wind_direction_10m") val windDirectionUnit: String,
    @field:SerializedName("cloud_cover") val cloudCoverUnits: String,
    @field:SerializedName("time") val time: String,
)

