package com.anael.samples.apps.windradar.compose.weather.model

data class DailyUiItem(
    val time: String,
    val tempMinText: String,
    val tempMaxText: String,
    val windSpeedText: String,
    val windGustText: String,
    val rainText: String,
    val brightnessFactor: Float,
    val emoji: String
)