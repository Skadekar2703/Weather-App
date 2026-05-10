package com.tommy.weatherapp.domain.model

import com.tommy.weatherapp.util.BackgroundState

data class WeatherDisplayModel(
    val locationName: String,
    val region: String,
    val currentTemperature: String,
    val feelsLike: String,
    val conditionText: String,
    val conditionIconUrl: String,
    val highLow: String,
    val backgroundState: BackgroundState,
    val detailItems: List<WeatherDetailItem>,
    val hourlyItems: List<HourlyForecastItem>,
    val dailyItems: List<DailyForecastItem>,
    val isDay: Boolean,
    val cacheTimestamp: Long,
    val offlineBannerText: String? = null,
)

data class WeatherDetailItem(
    val label: String,
    val value: String,
)

data class HourlyForecastItem(
    val timeLabel: String,
    val iconUrl: String,
    val temperature: String,
)

data class DailyForecastItem(
    val dayLabel: String,
    val iconUrl: String,
    val highLow: String,
    val rainChance: String,
)
