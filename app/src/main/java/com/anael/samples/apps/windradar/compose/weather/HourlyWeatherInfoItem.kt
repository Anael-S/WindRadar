package com.anael.samples.apps.windradar.compose.weather

import AutoResizeText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anael.samples.apps.windradar.R
import com.anael.samples.apps.windradar.compose.weather.model.HourlyUiItem

@Composable
fun HourlyWeatherInfoItem(
    item: HourlyUiItem,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
) {
    val skyBrush = rememberSkyGradient(item.brightnessFactor)

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
                    text = item.time,
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
                        text = item.tempText,
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
                        label = stringResource(R.string.wind_section),
                        value = item.windText,
                        unit = item.windUnit,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = Icons.Filled.Air,
                        label = stringResource(R.string.gusts_section),
                        value = item.gustText,
                        unit = item.windUnit,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = Icons.Filled.Cloud,
                        label = stringResource(R.string.cloud_section),
                        value = item.cloudsText,
                        unit = item.cloudUnit,
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
                            .rotate(item.windDirectionDeg.toFloat())
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${item.windDirectionDeg}°",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
