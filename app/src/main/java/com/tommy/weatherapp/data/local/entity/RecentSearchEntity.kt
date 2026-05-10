package com.tommy.weatherapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "city_name")
    val cityName: String,
    @ColumnInfo(name = "searched_at")
    val searchedAt: Long,
    val lat: Double? = null,
    val lng: Double? = null,
)
