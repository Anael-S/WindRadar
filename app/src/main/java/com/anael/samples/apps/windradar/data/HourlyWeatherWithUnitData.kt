package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the response of weather data search from a weather search API
 */
data class HourlyWeatherWithUnitData(
    @field:SerializedName("hourly")  val hourlyWeatherData: HourlyWeatherData,
    @field:SerializedName("hourly_units")  val hourlyUnits: HourlyUnitsData
)

