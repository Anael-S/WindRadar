package com.anael.samples.apps.windradar.data.local

import com.anael.samples.apps.windradar.data.local.AlertEntity
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun observeAlerts(): Flow<List<AlertEntity>>
    fun observeEnabledAlerts(): Flow<List<AlertEntity>>
    suspend fun upsert(alert: AlertEntity)
    suspend fun delete(id: String)
    suspend fun setEnabled(id: String, enabled: Boolean)
}
