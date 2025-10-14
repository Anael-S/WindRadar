package com.anael.samples.apps.windradar.data

import com.anael.samples.apps.windradar.api.WeatherService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WindRepository @Inject constructor(private val service: WeatherService) {

    fun getWindDataPrevision(latitude: Double,
                             longitude: Double,
                             timezone: String): Flow<WeatherWithUnitData> = flow {
        emit(service.fetchWindDataBasedOnLocation(
                latitude = latitude,
                longitude = longitude,
                timezone = timezone
        ))

    }
}
