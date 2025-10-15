package com.anael.samples.apps.windradar.ui.weather

import AutoResizeText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anael.samples.apps.windradar.compose.weather.TemperatureBand
import com.anael.samples.apps.windradar.compose.weather.WeatherStatItem
import com.anael.samples.apps.windradar.compose.weather.rememberSkyGradient
import com.anael.samples.apps.windradar.compose.weather.valueWithUnit
import com.anael.samples.apps.windradar.data.DailyUnitsData

@Composable
fun DailyWeatherInfoItem(
    date: String,
    tempMin: Double,
    tempMax: Double,
    windSpeedMax: Double,
    windGustMax: Double,
    rainSum: Double,
    units: DailyUnitsData,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    daylight: Float = 0.75f
) {
    val skyBrush = rememberSkyGradient(daylight)

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(skyBrush)
            .padding(10.dp)
    ) {
        Card(
            shape = RoundedCornerShape(cornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,

                )

                Spacer(Modifier.height(8.dp))

                TemperatureBand(
                    minText = valueWithUnit(tempMin, units.temperatureUnit, 0),
                    maxText = valueWithUnit(tempMax, units.temperatureUnit, 0)
                )

                Spacer(Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    WeatherStatItem(
                        icon = Icons.Filled.Air,
                        label = "Wind",
                        value = valueWithUnit(windSpeedMax, units.windSpeedsUnit, 1),
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = Icons.Filled.Air,
                        label = "Gusts",
                        value = valueWithUnit(windGustMax, units.windGustsUnit, 1),
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = Icons.Filled.WaterDrop,
                        label = "Rain",
                        value = valueWithUnit(rainSum, units.rainSumUnit, 1),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
