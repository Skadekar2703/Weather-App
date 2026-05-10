package com.tommy.weatherapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_weather")
data class CachedWeatherEntity(
    @PrimaryKey
    @ColumnInfo(name = "cache_key")
    val cacheKey: String,
    @ColumnInfo(name = "json_blob")
    val jsonBlob: String,
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long,
    @ColumnInfo(name = "location_label")
    val locationLabel: String,
)
