package com.anael.samples.apps.windradar.di

import com.anael.samples.apps.windradar.api.CitySuggestionService
import com.anael.samples.apps.windradar.api.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideWeatherService(): WeatherService {
        return WeatherService.create()
    }

    @Singleton
    @Provides
    fun provideCitySuggestionService(): CitySuggestionService {
        return CitySuggestionService.create()
    }
}