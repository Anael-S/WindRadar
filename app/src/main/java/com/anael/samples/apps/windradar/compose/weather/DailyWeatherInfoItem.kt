package com.anael.samples.apps.windradar.compose.weather

import AutoResizeText
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    modifier: Modifier = Modifier
) {
    val cardColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
    val textColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier
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
            // Date
            Text(
                text = date,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Temps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    icon = Icons.Filled.ArrowDownward,
                    label = "Min",
                    value = valueWithUnit(tempMin, units.temperatureUnit, 1)
                )
                StatItem(
                    icon = Icons.Filled.ArrowUpward,
                    label = "Max",
                    value = valueWithUnit(tempMax, units.temperatureUnit, 1)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Wind
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    icon = Icons.Filled.Air,
                    label = "Wind",
                    value = valueWithUnit(windSpeedMax, units.windSpeedsUnit, 1)
                )
                StatItem(
                    icon = Icons.Filled.Air,
                    label = "Gusts",
                    value = valueWithUnit(windGustMax, units.windGustsUnit, 1)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Rain
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    icon = Icons.Filled.WaterDrop,
                    label = "Rain",
                    value = valueWithUnit(rainSum, units.rainSumUnit, 1)
                )
            }
        }
    }
}

private fun valueWithUnit(value: Double, unit: String, decimals: Int = 1): String {
    val fmt = "%.${decimals}f"
    return String.format(fmt, value) + "\u00A0" + unit // NBSP keeps value+unit together
}

/**
 * Same visual language as your WindInfoItem, but generic for any daily stat.
 * If you prefer, you can just reuse your WindInfoItem by passing label/value.
 */
@Composable
private fun StatItem(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.widthIn(min = 88.dp)
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
