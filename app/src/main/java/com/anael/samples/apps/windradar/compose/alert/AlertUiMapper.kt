package com.anael.samples.apps.windradar.compose.alert

import android.content.Context
import com.anael.samples.apps.windradar.R
import com.anael.samples.apps.windradar.compose.alert.model.Alert
import com.anael.samples.apps.windradar.data.local.AlertEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlertUiMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun mapEntity(entity: AlertEntity): Alert {
        val title = entity.name ?: context.getString(R.string.alert)

        val windPart = " • " + context.getString(R.string.label_wind_threshold, entity.windMin)
        val gustPart = if (entity.gustMin > 0) {
            " • " + context.getString(R.string.label_gusts_threshold, entity.gustMin)
        } else ""

        val timePart = " • ${entity.startHour}:00–${entity.endHour}:00"

        val summary = buildString {
            append(windPart)
            append("\n")
            append(gustPart)
            append("\n")
            append(timePart)
        }

        return Alert(
            id = entity.id,
            title = title,
            enabled = entity.enabled,
            summary = summary
        )
    }

    fun mapList(list: List<AlertEntity>): List<Alert> =
        list.map { mapEntity(it) }
}