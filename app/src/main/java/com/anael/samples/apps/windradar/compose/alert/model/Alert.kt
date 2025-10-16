package com.anael.samples.apps.windradar.compose.alert.model

data class Alert(
    val id: String,
    val title: String,
    val enabled: Boolean,
    val summary: String,   // e.g., "Wind ≥ 25 km/h • 06:00–18:00 • Dir 180–240°"
)