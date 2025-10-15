package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.data.UiState
import com.anael.samples.apps.windradar.data.WeatherWithUnitData
import com.anael.samples.apps.windradar.data.WindRepository
import com.anael.samples.apps.windradar.data.CitySelectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import java.time.ZoneId

@HiltViewModel
class WindViewModel @Inject constructor(
    private val repository: WindRepository,
    cityRepo: CitySelectionRepository
) : ViewModel() {

    private val refresh = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val weatherState: StateFlow<UiState<WeatherWithUnitData>> =
        combine(
            // emit once at start and on every manual refresh
            refresh.onStart { emit(Unit) },
            // latest persisted city selection
            cityRepo.selectedCity.distinctUntilChanged()
        ) { _, city -> city }
            .flatMapLatest { city ->
                if (city == null) {
                    flowOf(UiState.Error("No city selected"))
                } else {
                    val tz = city.timezone ?: runCatching { ZoneId.systemDefault().id }.getOrElse { "UTC" }
                    repository
                        .getWindDataPrevision(
                            latitude = city.latitude,
                            longitude = city.longitude,
                            timezone = tz
                        )
                        .map<WeatherWithUnitData, UiState<WeatherWithUnitData>> { UiState.Success(it) }
                        .onStart { emit(UiState.Loading) }
                        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Loading
            )

    fun refreshData() {
        refresh.tryEmit(Unit)
    }
}
