package com.anael.samples.apps.windradar.api

import com.anael.samples.apps.windradar.data.WindDataResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Used to connect to the Weather API to fetch the data
 */
interface WeatherService {

    @GET("forecast")
    suspend fun fetchWindDataBasedOnLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly:String = "wind_speed_10m,wind_gusts_10m",
        @Query("timezone") timezone: String,
    ): WindDataResponse

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/v1/"

        fun create(): WeatherService {
            //TODO: remove, for more logs:
            val logger = HttpLoggingInterceptor().apply { level = Level.BODY }
//            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()


            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService::class.java)
        }
    }
}