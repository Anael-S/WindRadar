package com.anael.samples.apps.windradar.ui.weather

import AutoResizeText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anael.samples.apps.windradar.compose.weather.WeatherStatItem
import com.anael.samples.apps.windradar.compose.weather.rememberSkyGradient
import com.anael.samples.apps.windradar.compose.weather.valueWithUnit
import com.anael.samples.apps.windradar.data.HourlyUnitsData

@Composable
fun HourlyWeatherInfoItem(
    time: String,
    speed: Double,
    gust: Double,
    temp: Double,
    windDirection: Int,
    cloudCover: Int,
    units: HourlyUnitsData,
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
                    text = time,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Thermostat, contentDescription = "Temperature", tint = MaterialTheme.colorScheme.primary)
                    AutoResizeText(
                        text = valueWithUnit(temp, units.temperatureUnit, 1),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                        minFontSizeSp = 14f,
                        maxFontSizeSp = 30f,
                        stepSp = 1f
                    )
                }

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
                        value = valueWithUnit(speed, units.windSpeedsUnit, 1),
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = Icons.Filled.Air,
                        label = "Gusts",
                        value = valueWithUnit(gust, units.windGustsUnit, 1),
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = Icons.Filled.Cloud,
                        label = "Clouds",
                        value = valueWithUnit(cloudCover, units.cloudCoverUnits),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowUpward,
                        contentDescription = "Wind direction",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(windDirection.toFloat())
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "$windDirection° ${units.windDirectionUnit}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
