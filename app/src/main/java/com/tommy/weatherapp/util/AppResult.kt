package com.tommy.weatherapp.util

sealed class AppResult<out T> {
    data class Success<T>(val data: T, val source: DataSource) : AppResult<T>()
    data class Error(
        val message: String,
        val errorType: ErrorType = ErrorType.UNKNOWN,
    ) : AppResult<Nothing>()
    data object Loading : AppResult<Nothing>()
}

enum class DataSource {
    REMOTE,
    CACHE,
}

enum class ErrorType {
    INVALID_CITY,
    INVALID_API_KEY,
    RATE_LIMITED,
    NO_INTERNET,
    SERVER,
    UNKNOWN,
}
