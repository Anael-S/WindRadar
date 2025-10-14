package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the city data from the city sugestion API
 */
data class GeoResultData(
    val name: String,
    val country: String,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timezone: String
)

