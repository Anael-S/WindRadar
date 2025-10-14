package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the response of weather data search from a weather search API
 */
data class WeatherWithUnitData(
    @field:SerializedName("hourly")  val hourlyWeatherData: WeatherData,
    @field:SerializedName("hourly_units")  val hourlyUnits: HourlyUnitsData
)

