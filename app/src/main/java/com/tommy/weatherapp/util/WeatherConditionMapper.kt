package com.tommy.weatherapp.util

object WeatherConditionMapper {
    fun map(code: Int, isDay: Boolean): BackgroundState {
        if (!isDay) return BackgroundState.NIGHT

        return when (code) {
            1000 -> BackgroundState.SUNNY
            1003, 1006, 1009, 1030, 1135, 1147 -> BackgroundState.CLOUDY
            in 1063..1201, in 1240..1282 -> BackgroundState.RAINY
            else -> BackgroundState.CLOUDY
        }
    }
}
