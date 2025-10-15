package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.data.*
import com.anael.samples.apps.windradar.domain.FilterUpcomingHourly
import com.anael.samples.apps.windradar.utilities.ListUtils.sliceBy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class ForecastMode { Daily, Hourly }

/** One UI stream, two possible payloads */
sealed interface ForecastResult {
    data class Hourly(val data: HourlyWeatherWithUnitData) : ForecastResult
    data class Daily(val data: DailyWeatherWithUnitData)   : ForecastResult
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WindRepository,
    cityRepo: CitySelectionRepository,
    private val filterUpcomingHourly: FilterUpcomingHourly
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
            refresh.onStart { emit(Unit) },               // trigger on start + manual refresh
            cityRepo.selectedCity.distinctUntilChanged(), // persisted city
            mode
        ) { _, city, mode -> city to mode }
            .flatMapLatest { (city, mode) ->
                if (city == null) {
                    flowOf(UiState.Error("No city selected"))
                } else {
                    val zoneId = runCatching {
                        ZoneId.of(city.timezone ?: ZoneId.systemDefault().id)
                    }.getOrElse { ZoneId.systemDefault() }

                    when (mode) {
                        ForecastMode.Hourly ->
                            repository.getHourlyWindDataPrevision(
                                latitude = city.latitude,
                                longitude = city.longitude,
                                timezone = zoneId.id
                            )
                                .map<HourlyWeatherWithUnitData, UiState<ForecastResult>> { raw ->
                                    // Build (index, time) pairs to reuse domain filter and recover indices
                                    val indexedTimes = raw.hourlyWeatherData.rawTime.mapIndexed { i, t -> i to t }

                                    val keptPairs = filterUpcomingHourly(
                                        items = indexedTimes,
                                        zone = zoneId,
                                        timeSelector = { it.second } // select the time string
                                    )
                                    val keptIdx = keptPairs.map { it.first }

                                    if (keptIdx.isEmpty()) {
                                        // Nothing to keep (edge case) — return raw to avoid empty UI
                                        UiState.Success(ForecastResult.Hourly(raw))
                                    } else {
                                        val h = raw.hourlyWeatherData
                                        // Slice every parallel list with the same indices
                                        val filteredHourly = h.copy(
                                            rawTime = h.rawTime.sliceBy(keptIdx),
                                            // ↓ Ensure these names match your HourlyWeatherData fields
                                            temperature = h.temperature.sliceBy(keptIdx),
                                            windSpeeds = h.windSpeeds.sliceBy(keptIdx),
                                            windGusts = h.windGusts.sliceBy(keptIdx),
                                            windDirection = h.windDirection.sliceBy(keptIdx),
                                            cloudCover = h.cloudCover.sliceBy(keptIdx),
                                        )
                                        UiState.Success(ForecastResult.Hourly(raw.copy(hourlyWeatherData = filteredHourly)))
                                    }
                                }

                        ForecastMode.Daily ->
                            repository.getDailyWindDataPrevision(
                                latitude = city.latitude,
                                longitude = city.longitude,
                                timezone = zoneId.id
                            )
                                .map<DailyWeatherWithUnitData, UiState<ForecastResult>> {
                                    UiState.Success(ForecastResult.Daily(it))
                                }
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

