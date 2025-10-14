package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.data.WeatherData
import com.anael.samples.apps.windradar.data.WeatherWithUnitData
import com.anael.samples.apps.windradar.data.WindRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WindViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WindRepository
) : ViewModel() {

    private var timezone: String? = savedStateHandle["timezone"]
    private var lattitude: Double? = savedStateHandle["lattitude"]
    private var longitude: Double? = savedStateHandle["lattitude"]


    private val _weatherDataPrevisions = MutableStateFlow<WeatherWithUnitData?>(null)
    val weatherDataPrevisions: Flow<WeatherWithUnitData> get() = _weatherDataPrevisions.filterNotNull()

    init {
        refreshData()
    }


    fun refreshData() {
        viewModelScope.launch {
            try {

                //TODO: remove this, MOCKUP for now
                val lat = lattitude ?: 52.03634
                val lon = longitude ?: 4.32501
                val tz = timezone ?: "Europe/Amsterdam"
                _weatherDataPrevisions.value = repository.getWindDataPrevision(latitude = lat, longitude = lon, timezone = tz).first()

//                _windDataPrevisions.value = repository.getWindDataPrevision(latitude = lattitude!!, longitude = longitude!!, timezone = timezone ?: "").first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}