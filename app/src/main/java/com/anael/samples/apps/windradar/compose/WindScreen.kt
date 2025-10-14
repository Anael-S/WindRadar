package com.anael.samples.apps.windradar.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anael.samples.apps.windradar.data.HourlyUnitsData
import com.anael.samples.apps.windradar.data.WeatherData
import com.anael.samples.apps.windradar.data.WeatherWithUnitData
import com.google.samples.apps.sunflower.R
import com.anael.samples.apps.windradar.viewmodels.WindViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun WindScreen(viewModel: WindViewModel = hiltViewModel()) {
    WindScreen(
        weatherData = viewModel.weatherDataPrevisions,
        onPullToRefresh = viewModel::refreshData
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WindScreen(
    weatherData: Flow<WeatherWithUnitData>,
    onPullToRefresh: () -> Unit
) {
    val data = weatherData.collectAsState(initial = null)
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold { padding ->
        if (pullToRefreshState.isRefreshing) {
            onPullToRefresh()
        }

        Box(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            data.value?.let { weatherAndUnits ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        all = dimensionResource(id = R.dimen.card_side_margin)
                    )
                ) {
                    items(weatherAndUnits.hourlyWeatherData.time.size) { index ->
                        val time = weatherAndUnits.hourlyWeatherData.time[index]
                        val speed = weatherAndUnits.hourlyWeatherData.windSpeeds[index]
                        val gust = weatherAndUnits.hourlyWeatherData.windGusts[index]
                        val temp = weatherAndUnits.hourlyWeatherData.temperature[index]
                        val windDirection = weatherAndUnits.hourlyWeatherData.windDirection[index]
                        val cloudCover = weatherAndUnits.hourlyWeatherData.cloudCover[index]

                        WindItem(
                            time = time,
                            speed = speed,
                            gust = gust,
                            temp = temp,
                            windDirection = windDirection,
                            cloudCover = cloudCover,
                            weatherUnit = weatherAndUnits.hourlyUnits
                        )
                    }
                }
            }

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState
            )
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
