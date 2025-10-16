package com.anael.samples.apps.windradar.di

import android.content.Context
import androidx.room.Room
import com.anael.samples.apps.windradar.data.local.AlertDao
import com.anael.samples.apps.windradar.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "windradar.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideAlertDao(db: AppDatabase): AlertDao = db.alertDao()
}
