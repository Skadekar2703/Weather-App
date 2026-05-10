package com.tommy.weatherapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object WeatherFormatters {
    private val hourFormat = SimpleDateFormat("ha", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

    fun formatHour(epochSeconds: Long): String = hourFormat.format(Date(epochSeconds * 1000))

    fun formatDay(epochSeconds: Long): String = dayFormat.format(Date(epochSeconds * 1000))

    fun formatTemperature(value: Double): String = "${value.toInt()}°"

    fun formatLastUpdated(cachedAtMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
        val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(nowMillis - cachedAtMillis).coerceAtLeast(0)
        return when {
            diffMinutes < 1 -> "Last updated just now"
            diffMinutes == 1L -> "Last updated 1 min ago"
            else -> "Last updated $diffMinutes min ago"
        }
    }

    fun formatLocation(lat: Double, lng: Double): String = "${lat.format(2)}, ${lng.format(2)}"

    private fun Double.format(decimals: Int): String = "%.${decimals}f".format(Locale.US, this)
}
