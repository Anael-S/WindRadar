package com.anael.samples.apps.windradar.compose.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Displays low & high temperature labels with a weather emoji in between,
 * chosen according to brightness (sunshine factor).
 *
 * @param minText formatted min temperature string, e.g. "12°C"
 * @param maxText formatted max temperature string, e.g. "24°C"
 * @param brightnessFactor [0f..1f] value, usually from sunshineToBrightnessFactor()
 */
@Composable
fun TemperatureItemWithIcon(
    minText: String,
    maxText: String,
    brightnessFactor: Float
) {
    // Pick an emoji that matches the sunshine/brightness factor
    val weatherEmoji = when {
        brightnessFactor < 0.15f -> "☁\uFE0F"   // ☁️  really gray
        brightnessFactor < 0.35f -> "\uD83C\uDF25\uFE0F"   // 🌥️  mostly cloudy
        brightnessFactor < 0.55f -> "\uD83C\uDF25"         // 🌥   partly cloudy (light variant)
        brightnessFactor < 0.75f -> "\uD83C\uDF24\uFE0F"  // 🌤️  bit sunny
        else -> "\uD83C\uDF1E"                            // 🌞  full sunny
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Weather emoji centered between Low & High
        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = weatherEmoji,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            )
        }

        // Top labels (LOW / HIGH)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "LOW",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "HIGH",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(4.dp))

        // Min and Max temperature values
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = minText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = maxText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
