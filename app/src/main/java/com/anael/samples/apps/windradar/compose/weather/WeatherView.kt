package com.anael.samples.apps.windradar.compose.weather


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.anael.samples.apps.windradar.compose.weather.model.DailyUiItem
import com.anael.samples.apps.windradar.compose.weather.model.HourlyUiItem
import com.anael.samples.apps.windradar.data.UiState
import com.anael.samples.apps.windradar.ui.weather.DailyWeatherInfoItem
import com.anael.samples.apps.windradar.viewmodels.ForecastMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContent(
    mode: ForecastMode,
    dailyState: UiState<List<DailyUiItem>>,
    hourlyState: UiState<List<HourlyUiItem>>,
    onPullToRefresh: () -> Unit,
) {
    val ptr = rememberPullToRefreshState()

    // When user pulls and state flips to refreshing, kick off the refresh
    LaunchedEffect(ptr.isRefreshing) {
        if (ptr.isRefreshing) onPullToRefresh()
    }

    // End refresh when the CURRENT mode's stream leaves Loading (either Success or Error)
    LaunchedEffect(mode, dailyState, hourlyState, ptr.isRefreshing) {
        if (!ptr.isRefreshing) return@LaunchedEffect
        val done = when (mode) {
            ForecastMode.Daily  -> dailyState !is UiState.Loading
            ForecastMode.Hourly -> hourlyState !is UiState.Loading
        }
        if (done) ptr.endRefresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // IMPORTANT: keep nestedScroll on a wrapper that exists in *all* states
            .nestedScroll(ptr.nestedScrollConnection)
    ) {
        when (mode) {
            ForecastMode.Daily  -> RenderDaily(dailyState)
            ForecastMode.Hourly -> RenderHourly(hourlyState)
        }

        // Show indicator while refreshing or being pulled
        if (ptr.isRefreshing || ptr.verticalOffset > 0f) {
            PullToRefreshContainer(
                state = ptr,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun RenderDaily(state: UiState<List<DailyUiItem>>) {
    when (state) {
        is UiState.Loading -> CenterLoader()
        is UiState.Error   -> CenterError(state.message)
        is UiState.Success -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.data) { item ->
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        DailyWeatherInfoItem(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderHourly(state: UiState<List<HourlyUiItem>>) {
    when (state) {
        is UiState.Loading -> CenterLoader()
        is UiState.Error   -> CenterError(state.message)
        is UiState.Success -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.data) { item ->
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        HourlyWeatherInfoItem(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun CenterLoader() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CenterError(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: $message")
    }
}
