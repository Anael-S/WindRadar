package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.compose.weather.WeatherUiMapper
import com.anael.samples.apps.windradar.compose.weather.model.DailyUiItem
import com.anael.samples.apps.windradar.compose.weather.model.HourlyUiItem
import com.anael.samples.apps.windradar.data.CitySelectionRepository
import com.anael.samples.apps.windradar.data.DailyWeatherWithUnitData
import com.anael.samples.apps.windradar.data.HourlyWeatherWithUnitData
import com.anael.samples.apps.windradar.data.UiState
import com.anael.samples.apps.windradar.data.WindRepository
import com.anael.samples.apps.windradar.domain.FilterUpcomingHourly
import com.anael.samples.apps.windradar.utilities.ListUtils.sliceBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.ZoneId
import javax.inject.Inject

enum class ForecastMode { Daily, Hourly }

/** One UI stream, two possible payloads (raw data, unchanged) */
sealed interface ForecastResult {
    data class Hourly(val data: HourlyWeatherWithUnitData) : ForecastResult
    data class Daily(val data: DailyWeatherWithUnitData)   : ForecastResult
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WindRepository,
    cityRepo: CitySelectionRepository,
    private val filterUpcomingHourly: FilterUpcomingHourly,
    private val uiMapper: WeatherUiMapper,
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

    private val weatherState: StateFlow<UiState<ForecastResult>> =
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
                                        timeSelector = { it.second } // the ISO time string
                                    )
                                    val keptIdx = keptPairs.map { it.first }

                                    if (keptIdx.isEmpty()) {
                                        UiState.Success(ForecastResult.Hourly(raw)) // avoid empty UI
                                    } else {
                                        val h = raw.hourlyWeatherData
                                        val filteredHourly = h.copy(
                                            rawTime      = h.rawTime.sliceBy(keptIdx),
                                            temperature  = h.temperature.sliceBy(keptIdx),
                                            windSpeeds   = h.windSpeeds.sliceBy(keptIdx),
                                            windGusts    = h.windGusts.sliceBy(keptIdx),
                                            windDirection= h.windDirection.sliceBy(keptIdx),
                                            cloudCover   = h.cloudCover.sliceBy(keptIdx),
                                        )
                                        UiState.Success(
                                            ForecastResult.Hourly(raw.copy(hourlyWeatherData = filteredHourly))
                                        )
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

    /**
     * Presentation stream for DAILY cards (UI-ready).
     * Contains formatted strings + brightnessFactor (computed from sunshine/daylight),
     * so composables remain dumb and just render.
     */
    val dailyUi: StateFlow<UiState<List<DailyUiItem>>> =
        weatherState
            .filterIsInstance<UiState.Success<ForecastResult>>()
            .map { success ->
                when (val res = success.data) {
                    is ForecastResult.Daily -> {
                        val uiItems = uiMapper.mapDaily(res.data)
                        UiState.Success(uiItems)
                    }
                    is ForecastResult.Hourly -> UiState.Loading
                }
            }
            .onStart { emit(UiState.Loading) }
            .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Loading
            )

    /**
     * Presentation stream for HOURLY cards (UI-ready).
     * Keeps symmetry; extend your mapper as you like (e.g., emoji per hour).
     */
    val hourlyUi: StateFlow<UiState<List<HourlyUiItem>>> =
        weatherState
            .filterIsInstance<UiState.Success<ForecastResult>>()
            .map { success ->
                when (val res = success.data) {
                    is ForecastResult.Hourly -> {
                        val uiItems = uiMapper.mapHourly(res.data)
                        UiState.Success(uiItems)
                    }
                    is ForecastResult.Daily -> UiState.Loading
                }
            }
            .onStart { emit(UiState.Loading) }
            .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Loading
            )

    fun refreshData() {
        refresh.tryEmit(Unit)
    }
}
