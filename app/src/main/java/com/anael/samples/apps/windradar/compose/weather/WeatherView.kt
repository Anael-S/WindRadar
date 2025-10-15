package com.anael.samples.apps.windradar.compose.weather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.anael.samples.apps.windradar.data.UiState
import com.anael.samples.apps.windradar.ui.weather.DailyWeatherInfoItem
import com.anael.samples.apps.windradar.ui.weather.HourlyWeatherInfoItem
import com.anael.samples.apps.windradar.viewmodels.ForecastResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContent(
    weatherState: UiState<ForecastResult>,
    onPullToRefresh: () -> Unit
) {
    // Pull-to-refresh gesture state
    val pullToRefreshState = rememberPullToRefreshState()

    // Track whether we are refreshing
    var isRefreshing by remember { mutableStateOf(false) }

    // Start refresh when user pulls
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing && !isRefreshing) {
            isRefreshing = true
            onPullToRefresh()
        }
    }

    // Stop refresh when API call finishes
    LaunchedEffect(weatherState) {
        if (isRefreshing && weatherState !is UiState.Loading) {
            isRefreshing = false
            pullToRefreshState.endRefresh()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            when (weatherState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(all = 8.dp)
                    ) {
                        when (val response = weatherState.data) {
                            is ForecastResult.Hourly -> {
                                val data = response.data.hourlyWeatherData
                                items(data.time.size) { index ->
                                    HourlyWeatherInfoItem(
                                        time = data.time[index],
                                        speed = data.windSpeeds[index],
                                        gust = data.windGusts[index],
                                        temp = data.temperature[index],
                                        windDirection = data.windDirection[index],
                                        cloudCover = data.cloudCover[index],
                                        units = response.data.hourlyUnits
                                    )
                                }
                            }
                            is ForecastResult.Daily  -> {
                                val dailyData = response.data.dailyWeatherData
                                items(dailyData.time.size) { index ->
                                    DailyWeatherInfoItem(
                                        date = dailyData.time[index],
                                        tempMin = dailyData.temperatureMin[index],
                                        tempMax = dailyData.temperatureMax[index],
                                        windSpeedMax = dailyData.windSpeeds[index],
                                        windGustMax = dailyData.windGusts[index],
                                        rainSum = dailyData.rainSum[index],
                                        sunshineDuration = dailyData.sunshineDuration[index],
                                        daylightDuration = dailyData.daylightDuration[index],
                                        units = response.data.dailyUnits
                                    )
                                }
                            }
                        }

                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: ${(weatherState as UiState.Error).message}")
                    }
                }
            }

            // Only show indicator if refreshing or pulled down
            if (pullToRefreshState.isRefreshing || pullToRefreshState.verticalOffset > 0f) {
                PullToRefreshContainer(
                    state = pullToRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
