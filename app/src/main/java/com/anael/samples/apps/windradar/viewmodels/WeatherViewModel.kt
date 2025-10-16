package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.compose.weather.WeatherUiMapper
import com.anael.samples.apps.windradar.compose.weather.model.DailyUiItem
import com.anael.samples.apps.windradar.compose.weather.model.HourlyUiItem
import com.anael.samples.apps.windradar.data.CitySelectionRepository
import com.anael.samples.apps.windradar.data.HourlyWeatherWithUnitData
import com.anael.samples.apps.windradar.data.UiState
import com.anael.samples.apps.windradar.data.WindRepository
import com.anael.samples.apps.windradar.data.model.CitySelection
import com.anael.samples.apps.windradar.domain.FilterUpcomingHourly
import com.anael.samples.apps.windradar.utilities.ListUtils.sliceBy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import java.time.ZoneId

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WindRepository,
    cityRepo: CitySelectionRepository,
    private val filterUpcomingHourly: FilterUpcomingHourly,
    private val uiMapper: WeatherUiMapper,
) : ViewModel() {

    private val refresh = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** DAILY UI stream — always loads (no mode). */
    val dailyUi: StateFlow<UiState<List<DailyUiItem>>> =
        combine(
            refresh.onStart { emit(Unit) },                 // trigger on start + manual refresh
            cityRepo.selectedCity                         // chosen city (nullable)
        ) { _, city -> city }
            .flatMapLatest { city ->
                if (city == null) {
                    flowOf(UiState.Error("No city selected"))
                } else {
                    val zoneId = city.zoneOrSystem()
                    repository.getDailyWindDataPrevision(
                        latitude = city.latitude,
                        longitude = city.longitude,
                        timezone = zoneId.id
                    )
                        .map { daily -> UiState.Success(uiMapper.mapDaily(daily)) }
                        .onStart<UiState<List<DailyUiItem>>> { emit(UiState.Loading) }
                        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Loading
            )

    /** HOURLY UI stream — always loads and filters past hours. */
    val hourlyUi: StateFlow<UiState<List<HourlyUiItem>>> =
        combine(
            refresh.onStart { emit(Unit) },
            cityRepo.selectedCity
        ) { _, city -> city }
            .flatMapLatest { city ->
                if (city == null) {
                    flowOf(UiState.Error("No city selected"))
                } else {
                    val zoneId = city.zoneOrSystem()
                    repository.getHourlyWindDataPrevision(
                        latitude = city.latitude,
                        longitude = city.longitude,
                        timezone = zoneId.id
                    )
                        .map { raw -> raw.filterFromCurrentHour(zoneId) }
                        .map { filtered -> UiState.Success(uiMapper.mapHourly(filtered)) }
                        .onStart<UiState<List<HourlyUiItem>>> { emit(UiState.Loading) }
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

    /* ---------------- helpers ---------------- */

    private fun CitySelection.zoneOrSystem(): ZoneId =
        runCatching { ZoneId.of(this.timezone ?: ZoneId.systemDefault().id) }
            .getOrElse { ZoneId.systemDefault() }

    /** Applies domain filter to drop hours before the start of the current hour. */
    private fun HourlyWeatherWithUnitData.filterFromCurrentHour(zone: ZoneId): HourlyWeatherWithUnitData {
        // Build (index, time) pairs so we can retain indices that pass the domain use-case filter
        val indexedTimes = hourlyWeatherData.rawTime.mapIndexed { i, t -> i to t }
        val keptPairs = filterUpcomingHourly(
            items = indexedTimes,
            zone = zone,
            timeSelector = { it.second } // ISO time string
        )
        val keptIdx = keptPairs.map { it.first }
        if (keptIdx.isEmpty()) return this // avoid empty UI if something went odd

        val h = hourlyWeatherData
        val filtered = h.copy(
            rawTime       = h.rawTime.sliceBy(keptIdx),
            temperature   = h.temperature.sliceBy(keptIdx),
            windSpeeds    = h.windSpeeds.sliceBy(keptIdx),
            windGusts     = h.windGusts.sliceBy(keptIdx),
            windDirection = h.windDirection.sliceBy(keptIdx),
            cloudCover    = h.cloudCover.sliceBy(keptIdx),
        )
        return copy(hourlyWeatherData = filtered)
    }
}
