package com.anael.samples.apps.windradar.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.anael.samples.apps.windradar.data.GeoResultData
import com.anael.samples.apps.windradar.data.HourlyUnitsData
import com.anael.samples.apps.windradar.data.UiState
import com.anael.samples.apps.windradar.data.WeatherWithUnitData
import com.anael.samples.apps.windradar.viewmodels.CitySuggestionViewModel
import com.anael.samples.apps.windradar.viewmodels.WindViewModel

@Composable
fun WindScreen(
    viewModel: WindViewModel = hiltViewModel(),
    citySuggestionViewModel: CitySuggestionViewModel = hiltViewModel(),
) {
    val weatherState by viewModel.weatherState.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            key("SuggestionField") {
                SuggestionAddressTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    viewModel = citySuggestionViewModel
                )
            }

            WeatherContent(
                weatherState = weatherState,
                onPullToRefresh = viewModel::refreshData,
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContent(
    weatherState: UiState<WeatherWithUnitData>,
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
                        val data = weatherState.data.hourlyWeatherData
                        items(data.time.size) { index ->
                            WindItem(
                                time = data.time[index],
                                speed = data.windSpeeds[index],
                                gust = data.windGusts[index],
                                temp = data.temperature[index],
                                windDirection = data.windDirection[index],
                                cloudCover = data.cloudCover[index],
                                weatherUnit = weatherState.data.hourlyUnits
                            )
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

@Composable
fun WindItem(
    time: String,
    speed: Double,
    gust: Double,
    temp: Double,
    windDirection: Int,
    cloudCover: Int,
    weatherUnit: HourlyUnitsData
) {
    val cardColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
    val textColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Time
            Text(
                text = time,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )

            Divider(modifier = Modifier.padding(vertical = 6.dp))

            // Wind info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WindInfoItem(
                    icon = Icons.Default.Air,
                    label = "Speed",
                    value = String.format("%.1f %s", speed, weatherUnit.windSpeedsUnit)
                )
                WindInfoItem(
                    icon = Icons.Default.Air,
                    label = "Gust",
                    value = String.format("%.1f %s", gust, weatherUnit.windGustsUnit)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Temp and clouds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WindInfoItem(
                    icon = Icons.Default.Thermostat,
                    label = "Temp",
                    value = String.format("%.1f %s", temp, weatherUnit.temperatureUnit)
                )
                WindInfoItem(
                    icon = Icons.Default.Cloud,
                    label = "Clouds",
                    value = "$cloudCover${weatherUnit.cloudCoverUnits}"
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Direction
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Wind Direction",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(windDirection.toFloat()),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${windDirection}° ${weatherUnit.windDirectionUnit}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun WindInfoItem(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.widthIn(min = 70.dp) // keeps width consistent
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SuggestionAddressTextField(
    modifier: Modifier = Modifier,
    viewModel: CitySuggestionViewModel,
) {
    val citySuggestions by viewModel.suggestions.collectAsState()
    val cityQuery by viewModel.query.collectAsState()
    var expanded by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier) {
        AddressInputField(
            text = cityQuery,
            onTextChange = { new ->
                viewModel.onQueryChanged(new)
                expanded = true
            }
        )

        SuggestionsDropdown(
            suggestions = citySuggestions,
            expanded = expanded,
            onSelect = { suggestion ->
                val fullAddress = listOfNotNull(
                    suggestion.name,
                    suggestion.admin1,
                    suggestion.country
                ).joinToString(", ")

                viewModel.onQueryChanged(fullAddress)
                expanded = false
                focusManager.clearFocus()
            }
        )
    }
}


@Composable
fun AddressInputField(
    text: String,
    onTextChange: (String) -> Unit,
) {
    // keep a local TextFieldValue to preserve composition/selection
    var localValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text))
    }

    // sync when external text changes (ViewModel -> UI)
    LaunchedEffect(text) {
        if (text != localValue.text) {
            // preserve selection if possible
            localValue = localValue.copy(text = text)
        }
    }

    OutlinedTextField(
        value = localValue,
        onValueChange = { newValue ->
            localValue = newValue
            onTextChange(newValue.text) // push String to ViewModel
        },
        label = { Text("Search") },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { /* no extra re-requesting */ },
        singleLine = true,
    )
}




@Composable
fun SuggestionsDropdown(
    suggestions: List<GeoResultData>?,
    expanded: Boolean,
    onSelect: (GeoResultData) -> Unit,
) {
    val list = suggestions ?: emptyList()
    DropdownMenu(
        expanded = expanded && list.isNotEmpty(),
        onDismissRequest = {},
        modifier = Modifier.fillMaxWidth(),
        properties = PopupProperties(focusable = false)
    ) {
        list.forEach { suggestion ->
            val displayText = listOfNotNull(
                suggestion.name,
                suggestion.admin1,
                suggestion.country
            ).joinToString(", ")

            Text(
                text = displayText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onSelect(suggestion) }
            )
        }
    }
}



