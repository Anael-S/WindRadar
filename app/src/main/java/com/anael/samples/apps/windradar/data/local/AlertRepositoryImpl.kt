package com.anael.samples.apps.windradar.data.local

import com.anael.samples.apps.windradar.data.local.AlertDao
import com.anael.samples.apps.windradar.data.local.AlertEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val dao: AlertDao
) : AlertRepository {
    override fun observeAlerts(): Flow<List<AlertEntity>> = dao.observeAll()
    override fun observeEnabledAlerts(): Flow<List<AlertEntity>> = dao.observeEnabled()
    override suspend fun upsert(alert: AlertEntity) = dao.upsert(alert)
    override suspend fun delete(id: String) = dao.deleteById(id)
    override suspend fun setEnabled(id: String, enabled: Boolean) = dao.setEnabled(id, enabled)
}
