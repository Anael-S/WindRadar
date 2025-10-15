package com.anael.samples.apps.windradar.data.model

import com.anael.samples.apps.windradar.data.GeoResultData

data class CitySelection(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
)
