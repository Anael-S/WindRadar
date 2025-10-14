package com.anael.samples.apps.windradar.api

import com.anael.samples.apps.windradar.data.CitySuggestionDataResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Used to connect to the City API to fetch the data
 */
interface CitySuggestionService {

    @GET("search")
    suspend fun fetchSuggestions(
        @Query("count") latitude: Int = 5,
        @Query("name") name: String,
    ): CitySuggestionDataResult

    companion object {
        private const val BASE_URL = "https://geocoding-api.open-meteo.com/v1/"

        fun create(): CitySuggestionService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()


            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CitySuggestionService::class.java)
        }
    }
}