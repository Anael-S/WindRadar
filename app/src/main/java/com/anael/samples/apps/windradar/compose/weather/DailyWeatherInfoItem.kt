package com.anael.samples.apps.windradar.ui.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anael.samples.apps.windradar.compose.weather.TemperatureItemWithIcon
import com.anael.samples.apps.windradar.compose.weather.WeatherStatItem
import com.anael.samples.apps.windradar.compose.weather.model.DailyUiItem
import com.anael.samples.apps.windradar.compose.weather.rememberSkyGradient

@Composable
fun DailyWeatherInfoItem(
    item: DailyUiItem,
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

                )

                Spacer(Modifier.height(8.dp))

                TemperatureItemWithIcon(
                    minText = item.tempMinText,
                    maxText = item.tempMaxText,
                    weatherEmoji = item.emoji
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
                        value = item.windSpeedText,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = Icons.Filled.Air,
                        label = "Gusts",
                        value = item.windGustText,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = Icons.Filled.WaterDrop,
                        label = "Rain",
                        value = item.rainText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
