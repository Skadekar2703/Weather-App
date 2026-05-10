package com.tommy.weatherapp.domain.repository

import com.google.gson.Gson
import com.tommy.weatherapp.BuildConfig
import com.tommy.weatherapp.data.local.dao.WeatherDao
import com.tommy.weatherapp.data.local.entity.CachedWeatherEntity
import com.tommy.weatherapp.data.local.entity.RecentSearchEntity
import com.tommy.weatherapp.data.remote.service.WeatherApiService
import com.tommy.weatherapp.domain.model.WeatherDisplayModel
import com.tommy.weatherapp.domain.model.WeatherMapper
import com.tommy.weatherapp.util.AppResult
import com.tommy.weatherapp.util.DataSource
import com.tommy.weatherapp.util.ErrorType
import com.tommy.weatherapp.util.NetworkMonitor
import com.tommy.weatherapp.util.WeatherFormatters
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

class WeatherRepository(
    private val api: WeatherApiService,
    private val dao: WeatherDao,
    private val gson: Gson,
    private val networkMonitor: NetworkMonitor,
) {
    fun observeRecentSearches(): Flow<List<RecentSearchEntity>> = dao.observeRecentSearches()

    suspend fun getWeather(query: String, saveAsRecent: Boolean = true): AppResult<WeatherDisplayModel> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) {
            return AppResult.Error("Enter a city to search.", ErrorType.INVALID_CITY)
        }
        if (BuildConfig.WEATHER_API_KEY.isBlank()) {
            return AppResult.Error(
                "Add weather.api.key to local.properties to fetch live weather.",
                ErrorType.INVALID_API_KEY,
            )
        }

        val cacheKey = cacheKeyFor(normalizedQuery)
        val cached = dao.getCache(cacheKey)

        if (cached != null && !isCacheStale(cached.cachedAt)) {
            return AppResult.Success(
                gson.fromJson(cached.jsonBlob, WeatherDisplayModel::class.java),
                DataSource.CACHE,
            )
        }

        if (!networkMonitor.isOnline()) {
            return cached?.toSuccess(gson)
                ?: AppResult.Error("No internet connection. Search again when you are back online.", ErrorType.NO_INTERNET)
        }

        return try {
            val response = api.getForecast(normalizedQuery)
            when {
                response.isSuccessful && response.body() != null -> {
                    val displayModel = WeatherMapper.toDisplayModel(response.body()!!)
                    dao.insertCache(
                        CachedWeatherEntity(
                            cacheKey = cacheKey,
                            jsonBlob = gson.toJson(displayModel),
                            cachedAt = System.currentTimeMillis(),
                            locationLabel = displayModel.locationName,
                        )
                    )
                    if (saveAsRecent) {
                        dao.insertRecentSearch(
                            RecentSearchEntity(
                                cityName = displayModel.locationName,
                                searchedAt = System.currentTimeMillis(),
                            )
                        )
                    }
                    AppResult.Success(displayModel, DataSource.REMOTE)
                }

                else -> handleHttpError(response.code(), cached)
            }
        } catch (_: IOException) {
            cached?.toSuccess(gson)
                ?: AppResult.Error("No internet connection. Search again when you are back online.", ErrorType.NO_INTERNET)
        }
    }

    suspend fun getWeatherForCoordinates(lat: Double, lng: Double): AppResult<WeatherDisplayModel> {
        val query = WeatherFormatters.formatLocation(lat, lng)
        return getWeather(query, saveAsRecent = false)
    }

    private fun handleHttpError(
        code: Int,
        cached: CachedWeatherEntity?,
    ): AppResult<WeatherDisplayModel> {
        val fallback = cached?.toSuccess(gson)
        return when (code) {
            400 -> AppResult.Error("We couldn't find that city. Try a different search.", ErrorType.INVALID_CITY)
            401 -> AppResult.Error("Weather service is not configured yet.", ErrorType.INVALID_API_KEY)
            429 -> AppResult.Error("Rate limit reached. Try again in a bit.", ErrorType.RATE_LIMITED)
            in 500..599 -> fallback
                ?: AppResult.Error("Weather service is having trouble right now.", ErrorType.SERVER)
            else -> fallback ?: AppResult.Error("Something went wrong while loading the forecast.")
        }
    }

    private fun CachedWeatherEntity.toSuccess(gson: Gson): AppResult.Success<WeatherDisplayModel> {
        val cachedModel = gson.fromJson(jsonBlob, WeatherDisplayModel::class.java).copy(
            offlineBannerText = WeatherFormatters.formatLastUpdated(cachedAt),
            cacheTimestamp = cachedAt,
        )
        return AppResult.Success(cachedModel, DataSource.CACHE)
    }

    private fun cacheKeyFor(query: String): String = query.trim().lowercase(Locale.US)

    private fun isCacheStale(cachedAt: Long): Boolean =
        System.currentTimeMillis() - cachedAt > TimeUnit.MINUTES.toMillis(30)
}
