package com.anael.samples.apps.windradar.compose.weather.model

data class HourlyUiItem(
    val time: String,
    val tempText: String,
    val tempUnit: String,
    val windText: String,
    val gustText: String,
    val windUnit: String,
    val cloudsText: String,
    val cloudUnit: String,
    val windDirectionDeg: Int,
    val brightnessFactor: Float,
    val emoji: String
)