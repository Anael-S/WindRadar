package com.anael.samples.apps.windradar.di

import com.anael.samples.apps.windradar.data.CitySelectionRepository
import com.anael.samples.apps.windradar.data.CitySelectionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CityModule {
  @Binds @Singleton
  abstract fun bindCityRepo(impl: CitySelectionRepositoryImpl): CitySelectionRepository
}