package com.tommy.weatherapp

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.tommy.weatherapp.data.local.AppDatabase
import com.tommy.weatherapp.data.remote.service.WeatherApiService
import com.tommy.weatherapp.domain.repository.WeatherRepository
import com.tommy.weatherapp.util.NetworkMonitor
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WeatherAppContainer(context: Context) {
    private val gson = Gson()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("key", BuildConfig.WEATHER_API_KEY)
                .build()
            chain.proceed(original.newBuilder().url(url).build())
        }
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/".toHttpUrl())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "skycast.db",
    ).build()

    private val weatherApiService = retrofit.create(WeatherApiService::class.java)

    val weatherRepository = WeatherRepository(
        api = weatherApiService,
        dao = database.weatherDao(),
        gson = gson,
        networkMonitor = NetworkMonitor(context.applicationContext),
    )
}
