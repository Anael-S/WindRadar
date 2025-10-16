package com.anael.samples.apps.windradar.compose.weather

import AutoResizeText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin


@Composable
fun WeatherStatItem(
    icon: ImageVector,
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .widthIn(min = 88.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        AutoResizeText(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            minFontSizeSp = 9f,
            maxFontSizeSp = 14f,
        )
        AutoResizeText(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            minFontSizeSp = 9f,
            maxFontSizeSp = 14f,
        )
        AutoResizeText(
            text = unit,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            minFontSizeSp = 9f,
            maxFontSizeSp = 14f,
        )
    }
}

@Composable
fun rememberSkyGradient(daylight: Float): Brush {
    val t = daylight.coerceIn(0f, 1f)
    val dayTop = Color(0xFF6EC3FF)
    val dayBottom = Color(0xFF4285F4)
    val duskTop = Color(0xFF5B6B95)
    val duskBottom = Color(0xFF1E2A44)
    val top = lerpColor(duskTop, dayTop, t)
    val bottom = lerpColor(duskBottom, dayBottom, t)
    return Brush.verticalGradient(listOf(top, bottom))
}

/**
 * Converts a day's sunshine duration (in seconds) and daylight duration (sunrise→sunset, in seconds)
 * into a perceptual brightness factor [0f..1f].
 *
 * @param sunshineSeconds   How many seconds of sunshine occurred (e.g. 25200 = 7h).
 * @param daylightSeconds   Total daylight available that day (e.g. 43200 = 12h).
 * @return brightness factor usable for gradients, alpha, etc.
 *
 * Behavior:
 *  - 0 → pitch dark (no sun)
 *  - 0.5 → about half the day sunny
 *  - 1 → fully bright day (all daylight was sunshine)
 */
fun sunshineToBrightnessFactor(
    sunshineSeconds: Double,
    daylightSeconds: Double
): Float {
    if (daylightSeconds <= 0.0) return 0f // avoid division by zero
    val ratio = (sunshineSeconds / daylightSeconds)
        .coerceIn(0.0, 1.0)

    // Apply a soft gamma to make medium-sunny days feel a bit brighter (human perception)
    val gamma = 0.7f
    val adjusted = ratio.toFloat().pow(1f / gamma)

    // Add a subtle ambient floor so even 0-sun days aren’t completely black
    val ambientMin = 0.15f

    return (ambientMin + (1f - ambientMin) * adjusted)
        .coerceIn(0f, 1f)
}

fun hourToDaylight(hour: Int, minute: Int): Float {
    val hourF = hour + minute / 60f
    // Sine curve: 0 at ~06:00 & ~18:00, peak 1 at 12:00
    val radians = PI * (hourF - 6f) / 12f
    return sin(radians).toFloat().coerceIn(0f, 1f)
}

private fun lerpColor(start: Color, end: Color, t: Float): Color =
    Color(
        red = start.red + (end.red - start.red) * t,
        green = start.green + (end.green - start.green) * t,
        blue = start.blue + (end.blue - start.blue) * t,
        alpha = start.alpha + (end.alpha - start.alpha) * t
    )

fun valueWithUnit(value: Double, decimals: Int = 1): String {
    val fmt = "%.${decimals}f"
    return String.format(fmt, value)
}

fun valueWithUnit(value: Int): String =
    "$value"