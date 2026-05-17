package com.tommy.weatherapp.domain.model

import com.tommy.weatherapp.data.remote.model.ForecastDayDto
import com.tommy.weatherapp.data.remote.model.WeatherResponse
import com.tommy.weatherapp.util.BackgroundState
import com.tommy.weatherapp.util.WeatherConditionMapper
import com.tommy.weatherapp.util.WeatherFormatters

object WeatherMapper {
    fun toDisplayModel(
        response: WeatherResponse,
        cacheTimestamp: Long = System.currentTimeMillis(),
        offlineBannerText: String? = null,
    ): WeatherDisplayModel {
        val current = response.current
        val today = response.forecast.forecastDays.firstOrNull()
        val isDay = current.isDay == 1
        val backgroundState = WeatherConditionMapper.map(current.condition.code, isDay)

        return WeatherDisplayModel(
            locationName = response.location.name,
            region = listOf(response.location.region, response.location.country)
                .filter { it.isNotBlank() }
                .joinToString(", "),
            currentTemperature = WeatherFormatters.formatTemperature(current.tempC),
            feelsLike = WeatherFormatters.formatTemperature(current.feelsLikeC),
            conditionText = current.condition.text,
            conditionIconUrl = normalizeIcon(current.condition.icon),
            highLow = today?.let {
                "H:${WeatherFormatters.formatTemperature(it.day.maxTempC)}  L:${WeatherFormatters.formatTemperature(it.day.minTempC)}"
            } ?: "--",
            backgroundState = backgroundState,
            detailItems = buildDetails(response.forecast.forecastDays.firstOrNull(), current),
            hourlyItems = buildHourlyItems(response),
            dailyItems = buildDailyItems(response.forecast.forecastDays),
            isDay = isDay,
            cacheTimestamp = cacheTimestamp,
            offlineBannerText = offlineBannerText,
        )
    }

    private fun buildDetails(
        today: ForecastDayDto?,
        current: com.tommy.weatherapp.data.remote.model.CurrentDto,
    ): List<WeatherDetailItem> = listOf(
        WeatherDetailItem("Feels like", WeatherFormatters.formatTemperature(current.feelsLikeC)),
        WeatherDetailItem("Humidity", "${current.humidity}%"),
        WeatherDetailItem("Wind", "${current.windKph.toInt()} km/h"),
        WeatherDetailItem("UV", current.uv.toInt().toString()),
        WeatherDetailItem("Visibility", "${current.visibilityKm.toInt()} km"),
        WeatherDetailItem("Sunrise", today?.astro?.sunrise.orEmpty()),
        WeatherDetailItem("Sunset", today?.astro?.sunset.orEmpty()),
    )

    private fun buildHourlyItems(response: WeatherResponse): List<HourlyForecastItem> {
        val currentEpoch = response.location.localtimeEpoch
        return response.forecast.forecastDays
            .flatMap { it.hour }
            .filter { it.timeEpoch >= currentEpoch }
            .take(12)
            .map {
                HourlyForecastItem(
                    timeLabel = WeatherFormatters.formatHour(it.timeEpoch),
                    iconUrl = normalizeIcon(it.condition.icon),
                    temperature = WeatherFormatters.formatTemperature(it.tempC),
                )
            }
    }

    private fun buildDailyItems(days: List<ForecastDayDto>): List<DailyForecastItem> =
        days.take(5).map {
            DailyForecastItem(
                dayLabel = WeatherFormatters.formatDay(it.dateEpoch),
                iconUrl = normalizeIcon(it.day.condition.icon),
                conditionText = it.day.condition.text,
                highLow = "${WeatherFormatters.formatTemperature(it.day.maxTempC)} / ${WeatherFormatters.formatTemperature(it.day.minTempC)}",
                rainChance = "${it.day.dailyChanceOfRain}%",
            )
        }

    private fun normalizeIcon(iconPath: String): String {
        if (iconPath.isBlank()) return ""
        return if (iconPath.startsWith("http")) iconPath else "https:$iconPath"
    }
}
