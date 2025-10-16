package com.anael.samples.apps.windradar.compose.weather


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    // Start refresh when user pulls
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing && !isRefreshing) {
            isRefreshing = true
            onPullToRefresh()
        }
    }
    // Stop refresh when either stream finishes loading
    LaunchedEffect(mode, dailyState, hourlyState) {
        val done = when (mode) {
            ForecastMode.Daily -> dailyState !is UiState.Loading
            ForecastMode.Hourly -> hourlyState !is UiState.Loading
        }
        if (isRefreshing && done) {
            isRefreshing = false
            pullToRefreshState.endRefresh()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        when (mode) {
            ForecastMode.Daily -> RenderDaily(dailyState)
            ForecastMode.Hourly -> RenderHourly(hourlyState)
        }

        if (pullToRefreshState.isRefreshing || pullToRefreshState.verticalOffset > 0f) {
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun RenderDaily(state: UiState<List<DailyUiItem>>) {
    when (state) {
        is UiState.Loading -> CenterLoader()
        is UiState.Error -> CenterError(state.message)
        is UiState.Success -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(state.data) { item ->
                    DailyWeatherInfoItem(item = item)
                }
            }
        }
    }
}

@Composable
private fun RenderHourly(state: UiState<List<HourlyUiItem>>) {
    when (state) {
        is UiState.Loading -> CenterLoader()
        is UiState.Error -> CenterError(state.message)
        is UiState.Success -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(state.data) { item ->
                    HourlyWeatherInfoItem(item = item)
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
