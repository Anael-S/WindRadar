package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import java.time.ZoneId

enum class ForecastMode { Daily, Hourly }

/** One UI stream, two possible payloads */
sealed interface ForecastResult {
    data class Hourly(val data: HourlyWeatherWithUnitData) : ForecastResult
    data class Daily(val data: DailyWeatherWithUnitData)   : ForecastResult
}

@HiltViewModel
class WindViewModel @Inject constructor(
    private val repository: WindRepository,
    cityRepo: CitySelectionRepository
) : ViewModel() {

    private val _mode = MutableStateFlow(ForecastMode.Daily)
    val mode: StateFlow<ForecastMode> = _mode

    fun setMode(newMode: ForecastMode) {
        if (_mode.value != newMode) {
            _mode.value = newMode
            refreshData()
        }
    }

    private val refresh = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** UiState now carries ForecastResult (Hourly or Daily) */
    val weatherState: StateFlow<UiState<ForecastResult>> =
        combine(
            refresh.onStart { emit(Unit) },                        // trigger on start + manual refresh
            cityRepo.selectedCity.distinctUntilChanged(),          // persisted city
            mode
        ) { _, city, mode -> city to mode }
            .flatMapLatest { (city, mode) ->
                if (city == null) {
                    flowOf(UiState.Error("No city selected"))
                } else {
                    val timezone = city.timezone ?: runCatching { ZoneId.systemDefault().id }.getOrElse { "UTC" }
                    when (mode) {
                        ForecastMode.Hourly ->
                            repository.getHourlyWindDataPrevision(
                                latitude = city.latitude,
                                longitude = city.longitude,
                                timezone = timezone
                            ).map<HourlyWeatherWithUnitData, UiState<ForecastResult>> { UiState.Success(ForecastResult.Hourly(it)) }

                        ForecastMode.Daily  ->
                            repository.getDailyWindDataPrevision(
                                latitude = city.latitude,
                                longitude = city.longitude,
                                timezone = timezone
                            ).map<DailyWeatherWithUnitData, UiState<ForecastResult>> { UiState.Success(ForecastResult.Daily(it)) }
                    }
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
