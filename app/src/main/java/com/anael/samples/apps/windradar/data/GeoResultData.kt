package com.anael.samples.apps.windradar.data

import com.anael.samples.apps.windradar.data.model.CitySelection

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
) {
    fun toCitySelection(): CitySelection {
        val fullName = listOfNotNull(
            name,
            admin1,
            country
        ).joinToString(", ")
        return CitySelection(
            name = fullName ?: "",
            latitude = latitude,
            longitude = longitude,
            timezone = timezone,
        )
    }
}

