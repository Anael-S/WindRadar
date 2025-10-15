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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin


@Composable
fun TemperatureBand(minText: String, maxText: String) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("LOW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("HIGH", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
        ) {
            Box(
                Modifier
                    .size(14.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(7.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                Modifier
                    .size(14.dp)
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(7.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(minText, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(maxText, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
@Composable
fun WeatherStatItem(
    icon: ImageVector,
    label: String,
    value: String,
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

fun computeDaylightFactor(raw: String): Float {
    // 1) Try full datetime formats
    val dtFormats = listOf(
        "yyyy-MM-dd'T'HH:mm",
        "dd/MM/yyyy HH:mm"
    ).map { DateTimeFormatter.ofPattern(it, Locale.getDefault()) }

    for (fmt in dtFormats) {
        try {
            val ldt = LocalDateTime.parse(raw, fmt)
            return hourToDaylight(ldt.hour, ldt.minute)
        } catch (_: Exception) {}
    }

    // 2) Try time-only directly (e.g., "06:00")
    try {
        val lt = LocalTime.parse(raw, DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
        return hourToDaylight(lt.hour, lt.minute)
    } catch (_: Exception) {}

    // 3) Extract time from any string (e.g., "Wednesday 06:00")
    val timeMatch = Regex("""\b(\d{1,2}):(\d{2})\b""").find(raw)
    if (timeMatch != null) {
        val (hStr, mStr) = timeMatch.destructured
        val h = hStr.toInt().coerceIn(0, 23)
        val m = mStr.toInt().coerceIn(0, 59)
        return hourToDaylight(h, m)
    }

    // 4) Last resort: assume noon
    return 1f
}

private fun hourToDaylight(hour: Int, minute: Int): Float {
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

fun valueWithUnit(value: Double, unit: String, decimals: Int = 1): String {
    val fmt = "%.${decimals}f"
    return String.format(fmt, value) + "\u00A0" + unit
}

fun valueWithUnit(value: Int, unit: String): String =
    "$value\u00A0$unit"