package com.anael.samples.apps.windradar.compose.weather.model

data class DailyUiItem(
    val time: String,
    val tempMinText: String,
    val tempUnit: String,
    val tempMaxText: String,
    val windSpeedText: String,
    val windGustText: String,
    val windUnit: String,
    val rainText: String,
    val rainUnit: String,
    val brightnessFactor: Float,
    val emoji: String
)