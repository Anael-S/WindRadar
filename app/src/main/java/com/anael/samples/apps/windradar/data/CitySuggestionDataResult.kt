package com.anael.samples.apps.windradar.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the response of a city from a city suggestion search API
 */
data class CitySuggestionDataResult(
    @field:SerializedName("results")  val citySuggested : List<GeoResultData>,
)

