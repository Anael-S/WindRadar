package com.anael.samples.apps.windradar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "alerts",
    indices = [Index(value = ["enabled"]), Index(value = ["createdAt"])]
)
data class AlertEntity(
    @PrimaryKey val id: String,             // UUID string
    val name: String?,
    val windMin: Float,
    val gustMin: Float,
    val startHour: Int,                     // 0..23
    val endHour: Int,                       // 0..23
    val enabled: Boolean = true,
    val createdAt: Long,                    // epoch millis
    // Optional: bind to a city if needed
    val cityName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)
