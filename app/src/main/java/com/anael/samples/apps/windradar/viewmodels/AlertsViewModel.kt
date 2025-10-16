package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.compose.alert.AlertDraft
import com.anael.samples.apps.windradar.compose.alert.AlertUiMapper
import com.anael.samples.apps.windradar.compose.alert.model.Alert
import com.anael.samples.apps.windradar.data.local.AlertEntity
import com.anael.samples.apps.windradar.data.local.AlertRepository
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
    private val uiMapper: AlertUiMapper
) : ViewModel() {

    val alertsUi: StateFlow<List<Alert>> =
        repository.observeAlerts()
            .map { uiMapper.mapList(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createAlert(draft: AlertDraft) {
        viewModelScope.launch {
            val entity = draft.toEntity()
            repository.upsert(entity)
        }
    }

    fun toggle(id: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.setEnabled(id, enabled)
        }
    }

    fun delete(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }
}

private fun AlertDraft.toEntity(): AlertEntity = AlertEntity(
    id = UUID.randomUUID().toString(),
    name = name.ifBlank { null },
    windMin = windMin,
    gustMin = gustMin,
    dirStart = dirStart,
    dirEnd = dirEnd,
    startHour = startHour,
    endHour = endHour,
    enabled = true,
    createdAt = System.currentTimeMillis()
)
