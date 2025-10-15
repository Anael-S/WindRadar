package com.anael.samples.apps.windradar.data

import androidx.datastore.core.DataStore
import com.anael.samples.apps.windradar.datastore.proto.CitySelectionProto
import com.anael.samples.apps.windradar.data.model.CitySelection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CitySelectionRepositoryImpl @Inject constructor(
  private val store: DataStore<CitySelectionProto>
) : CitySelectionRepository {

  override val selectedCity: Flow<CitySelection?> =
    store.data.map { p ->
      if (p.name.isBlank() || (p.latitude == 0.0 && p.longitude == 0.0)) null
      else CitySelection(
        name        = p.name,
        latitude    = p.latitude,
        longitude   = p.longitude,
        timezone    = p.timezone.ifBlank { null },
      )
    }

  override suspend fun saveCity(city: CitySelection) {
    store.updateData { cur ->
      cur.toBuilder()
        .setName(city.name)
        .setLatitude(city.latitude)
        .setLongitude(city.longitude)
        .setTimezone(city.timezone ?: "")
        .build()
    }
  }
}
