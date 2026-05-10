package com.tommy.weatherapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val location: LocationDto = LocationDto(),
    val current: CurrentDto = CurrentDto(),
    val forecast: ForecastDto = ForecastDto(),
)

data class LocationDto(
    val name: String = "",
    val region: String = "",
    val country: String = "",
    @SerializedName("localtime_epoch")
    val localtimeEpoch: Long = 0L,
)

data class CurrentDto(
    @SerializedName("temp_c")
    val tempC: Double = 0.0,
    @SerializedName("is_day")
    val isDay: Int = 1,
    val condition: ConditionDto = ConditionDto(),
    @SerializedName("feelslike_c")
    val feelsLikeC: Double = 0.0,
    val humidity: Int = 0,
    @SerializedName("wind_kph")
    val windKph: Double = 0.0,
    val uv: Double = 0.0,
    @SerializedName("vis_km")
    val visibilityKm: Double = 0.0,
)

data class ForecastDto(
    @SerializedName("forecastday")
    val forecastDays: List<ForecastDayDto> = emptyList(),
)

data class ForecastDayDto(
    @SerializedName("date_epoch")
    val dateEpoch: Long = 0L,
    val day: DayDto = DayDto(),
    val astro: AstroDto = AstroDto(),
    val hour: List<HourDto> = emptyList(),
)

data class DayDto(
    @SerializedName("maxtemp_c")
    val maxTempC: Double = 0.0,
    @SerializedName("mintemp_c")
    val minTempC: Double = 0.0,
    @SerializedName("daily_chance_of_rain")
    val dailyChanceOfRain: Int = 0,
    val condition: ConditionDto = ConditionDto(),
)

data class AstroDto(
    val sunrise: String = "",
    val sunset: String = "",
)

data class HourDto(
    @SerializedName("time_epoch")
    val timeEpoch: Long = 0L,
    @SerializedName("temp_c")
    val tempC: Double = 0.0,
    val condition: ConditionDto = ConditionDto(),
)

data class ConditionDto(
    val text: String = "",
    val icon: String = "",
    val code: Int = 1000,
)
