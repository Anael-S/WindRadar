package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the response of weather data search from a weather search API
 */
data class DailyWeatherWithUnitData(
    @field:SerializedName("daily")  val dailyWeatherData: DailyWeatherData,
    @field:SerializedName("daily_units")  val dailyUnits: DailyUnitsData
)

