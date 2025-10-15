package com.anael.samples.apps.windradar.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.anael.samples.apps.windradar.datastore.proto.CitySelectionProto
import java.io.InputStream
import java.io.OutputStream

object CitySelectionSerializer : Serializer<CitySelectionProto> {
  override val defaultValue: CitySelectionProto = CitySelectionProto.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): CitySelectionProto =
    try { CitySelectionProto.parseFrom(input) }
    catch (e: Exception) { throw CorruptionException("Cannot read CitySelectionProto.", e) }

  override suspend fun writeTo(t: CitySelectionProto, output: OutputStream) = t.writeTo(output)
}