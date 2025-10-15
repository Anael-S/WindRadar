package com.anael.samples.apps.windradar.data

import com.anael.samples.apps.windradar.api.WeatherService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WindRepository @Inject constructor(private val service: WeatherService) {

    fun getHourlyWindDataPrevision(latitude: Double,
                                   longitude: Double,
                                   timezone: String): Flow<HourlyWeatherWithUnitData> = flow {
        emit(service.fetchHourlyWindDataBasedOnLocation(
                latitude = latitude,
                longitude = longitude,
                timezone = timezone
        ))
    }

    fun getDailyWindDataPrevision(latitude: Double,
                                   longitude: Double,
                                   timezone: String): Flow<DailyWeatherWithUnitData> = flow {
        emit(service.fetchDailyWindDataBasedOnLocation(
            latitude = latitude,
            longitude = longitude,
            timezone = timezone
        ))
    }
}
