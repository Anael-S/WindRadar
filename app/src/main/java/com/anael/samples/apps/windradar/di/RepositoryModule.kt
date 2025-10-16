package com.anael.samples.apps.windradar.di

import com.anael.samples.apps.windradar.data.local.AlertRepository
import com.anael.samples.apps.windradar.data.local.AlertRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository
}
