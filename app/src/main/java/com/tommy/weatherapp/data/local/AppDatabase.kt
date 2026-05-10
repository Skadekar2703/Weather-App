package com.tommy.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tommy.weatherapp.data.local.dao.WeatherDao
import com.tommy.weatherapp.data.local.entity.CachedWeatherEntity
import com.tommy.weatherapp.data.local.entity.RecentSearchEntity

@Database(
    entities = [CachedWeatherEntity::class, RecentSearchEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
