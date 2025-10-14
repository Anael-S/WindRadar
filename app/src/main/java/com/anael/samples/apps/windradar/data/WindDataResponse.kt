package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the response of wind data from a weather search API
 */
data class WindDataResponse(
    @field:SerializedName("hourly")  val hourly: WindData
)

