package com.anael.samples.apps.windradar.compose.weather

import AutoResizeText
import android.R.attr.fontWeight
import android.R.style
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anael.samples.apps.windradar.data.HourlyUnitsData


@Composable
fun HourlyWindItem(
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
                val gustText = buildString {
                    append(String.format("%.1f", gust))
                    append('\u00A0') // NBSP so value+unit stay on one line
                    append(weatherUnit.windGustsUnit)
                }
                val speedText = buildString {
                    append(String.format("%.1f", speed))
                    append('\u00A0') // NBSP so value+unit stay on one line
                    append(weatherUnit.windSpeedsUnit)
                }
                WindInfoItem(
                    icon = Icons.Default.Air,
                    label = "Speed",
                    value = speedText
                )
                WindInfoItem(
                    icon = Icons.Default.Air,
                    label = "Gust",
                    value = gustText
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Temp and clouds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tempText = buildString {
                    append(String.format("%.1f", temp))
                    append('\u00A0') // NBSP so value+unit stay on one line
                    append(weatherUnit.temperatureUnit)
                }
                val cloudText = buildString {
                    append(String.format("%d", cloudCover))
                    append('\u00A0') // NBSP so value+unit stay on one line
                    append(weatherUnit.cloudCoverUnits)
                }
                WindInfoItem(
                    icon = Icons.Default.Thermostat,
                    label = "Temp",
                    value = tempText
                )
                WindInfoItem(
                    icon = Icons.Default.Cloud,
                    label = "Clouds",
                    value = cloudText
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
        AutoResizeText(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            minFontSizeSp = 10f,
            maxFontSizeSp = 14f,
            stepSp = 1f
        )
    }
}



