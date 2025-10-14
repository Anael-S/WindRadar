package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the wind data from a weather search API
 */
data class WindData(
    @field:SerializedName("wind_speed_10m")  val windSpeeds: List<Double>,
    @field:SerializedName("wind_gusts_10m") val windGusts: List<Double>,
    @field:SerializedName("time") val time: List<String>
)

