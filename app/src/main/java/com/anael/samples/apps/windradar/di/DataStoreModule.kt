package com.anael.samples.apps.windradar.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.anael.samples.apps.windradar.datastore.CitySelectionSerializer
import com.anael.samples.apps.windradar.datastore.proto.CitySelectionProto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

  @Provides @Singleton
  fun provideCitySelectionDataStore(
    @ApplicationContext context: Context
  ): DataStore<CitySelectionProto> =
    DataStoreFactory.create(
      serializer = CitySelectionSerializer,
      produceFile = { File(context.filesDir, "city_selection.pb") }

    )
}
