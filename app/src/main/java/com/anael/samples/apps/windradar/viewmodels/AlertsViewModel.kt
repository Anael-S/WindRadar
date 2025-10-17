package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.compose.alert.AlertDraft
import com.anael.samples.apps.windradar.compose.alert.AlertUiMapper
import com.anael.samples.apps.windradar.compose.alert.model.Alert
import com.anael.samples.apps.windradar.data.local.AlertEntity
import com.anael.samples.apps.windradar.data.local.AlertRepository
import com.anael.samples.apps.windradar.workers.AlertCheckLauncher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: AlertRepository,
    private val uiMapper: AlertUiMapper,
    private val alertCheckLauncher: AlertCheckLauncher
) : ViewModel() {

    val alertsUi: StateFlow<List<Alert>> =
        repository.observeAlerts()
            .map { uiMapper.mapList(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createOrUpdateAlert(draft: AlertDraft) {
        viewModelScope.launch {
            val entity = draft.toEntity()
            repository.upsert(entity)
        }
    }

    fun enableAlert(id: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.setEnabled(id, enabled)
        }
    }

    fun deleteAlert(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }

    fun createOrUpdateAlertAndRunCheck(draft: AlertDraft) =
        viewModelScope.launch {
            val entity = draft.toEntity()
            repository.upsert(entity)
            alertCheckLauncher.runNow()                       // trigger the worker once
        }
}

private fun AlertDraft.toEntity(): AlertEntity = AlertEntity(
    id = UUID.randomUUID().toString(),
    name = name.ifBlank { null },
    windMin = windMin,
    gustMin = gustMin,
    startHour = startHour,
    endHour = endHour,
    enabled = true,
    createdAt = System.currentTimeMillis()
)
