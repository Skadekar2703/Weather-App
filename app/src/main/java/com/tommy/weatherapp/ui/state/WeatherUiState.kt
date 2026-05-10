package com.tommy.weatherapp.ui.state

import com.tommy.weatherapp.domain.model.WeatherDisplayModel

sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val data: WeatherDisplayModel) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
    data object Empty : WeatherUiState()
}
