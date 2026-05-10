package com.tommy.weatherapp.data.remote.service

import com.tommy.weatherapp.data.remote.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") location: String,
        @Query("days") days: Int = 7,
        @Query("aqi") airQuality: String = "no",
        @Query("alerts") alerts: String = "no",
    ): Response<WeatherResponse>
}
