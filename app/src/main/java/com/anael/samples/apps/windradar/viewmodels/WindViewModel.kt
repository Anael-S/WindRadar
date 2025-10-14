package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.data.UiState
import com.anael.samples.apps.windradar.data.WeatherWithUnitData
import com.anael.samples.apps.windradar.data.WindRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WindViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WindRepository
) : ViewModel() {

    private val timezone: String = savedStateHandle["timezone"] ?: "Europe/Amsterdam"
    private val latitude: Double = savedStateHandle["latitude"] ?: 52.03634
    private val longitude: Double = savedStateHandle["longitude"] ?: 4.32501

    private val _weatherState = MutableStateFlow<UiState<WeatherWithUnitData>>(UiState.Loading)
    val weatherState: StateFlow<UiState<WeatherWithUnitData>> = _weatherState

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _weatherState.value = UiState.Loading
            try {
                val data = repository
                    .getWindDataPrevision(latitude = latitude, longitude = longitude, timezone = timezone)
                    .first() // collect only one result from the repository
                _weatherState.value = UiState.Success(data)
            } catch (e: Exception) {
                _weatherState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
