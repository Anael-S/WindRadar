package com.anael.samples.apps.windradar.compose.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anael.samples.apps.windradar.compose.weather.model.DailyUiItem
import com.anael.samples.apps.windradar.compose.weather.model.HourlyUiItem
import com.anael.samples.apps.windradar.data.UiState
import com.anael.samples.apps.windradar.ui.weather.DailyWeatherInfoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContent(
    dailyState: UiState<List<DailyUiItem>>,
    hourlyState: UiState<List<HourlyUiItem>>,
    onPullToRefresh: () -> Unit,
) {
    val ptr = rememberPullToRefreshState()

    // Start refresh when user pulls
    LaunchedEffect(ptr.isRefreshing) {
        if (ptr.isRefreshing) onPullToRefresh()
    }

    // End refresh when BOTH sections are not loading (either Success or Error)
    LaunchedEffect(dailyState, hourlyState, ptr.isRefreshing) {
        if (!ptr.isRefreshing) return@LaunchedEffect
        val done = (dailyState !is UiState.Loading) && (hourlyState !is UiState.Loading)
        if (done) ptr.endRefresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(ptr.nestedScrollConnection) // live in all states
    ) {
        // Single vertically scrollable screen with two sections
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // ---- Daily section ----
            item {
                Spacer(Modifier.height(12.dp))
                SectionHeader(text = "Daily")
            }
            item {
                RenderDailyRow(dailyState)
            }

            // ---- Hourly section ----
            item {
                SectionHeader(text = "Hourly")
            }
            item {
                RenderHourlyRow(hourlyState)
            }

            // Extra bottom space
            item { Spacer(Modifier.height(24.dp)) }
        }

        // Pull-to-refresh indicator
        if (ptr.isRefreshing || ptr.verticalOffset > 0f) {
            PullToRefreshContainer(
                state = ptr,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

/* ----------------- Section UI ----------------- */

@Composable
private fun SectionHeader(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun RenderHourlyRow(state: UiState<List<HourlyUiItem>>) {
    when (state) {
        is UiState.Loading -> CenterLoader(minHeight = 140.dp)
        is UiState.Error   -> CenterError(state.message, minHeight = 140.dp)
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
private fun RenderDailyRow(state: UiState<List<DailyUiItem>>) {
    when (state) {
        is UiState.Loading -> CenterLoader(minHeight = 140.dp)
        is UiState.Error   -> CenterError(state.message, minHeight = 140.dp)
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

/* ----------------- Helpers ----------------- */

@Composable
private fun CenterLoader(minHeight: Dp = 0.dp) {
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CenterError(message: String, minHeight: Dp = 0.dp) {
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight),
        contentAlignment = Alignment.Center
    ) {
        Text("Error: $message")
    }
}
