package com.tommy.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommy.weatherapp.data.local.entity.CachedWeatherEntity
import com.tommy.weatherapp.data.local.entity.RecentSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM cached_weather WHERE cache_key = :cacheKey LIMIT 1")
    suspend fun getCache(cacheKey: String): CachedWeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(entity: CachedWeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(entity: RecentSearchEntity)

    @Query("SELECT * FROM recent_searches ORDER BY searched_at DESC LIMIT 6")
    fun observeRecentSearches(): Flow<List<RecentSearchEntity>>
}
