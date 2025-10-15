package com.anael.samples.apps.windradar.di

import com.anael.samples.apps.windradar.domain.FilterUpcomingHourly
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun provideFilterUpcomingHourly(): FilterUpcomingHourly = FilterUpcomingHourly()
}
