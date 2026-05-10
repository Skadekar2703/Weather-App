package com.tommy.weatherapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tommy.weatherapp.data.local.entity.RecentSearchEntity
import com.tommy.weatherapp.domain.repository.WeatherRepository
import com.tommy.weatherapp.ui.state.WeatherUiState
import com.tommy.weatherapp.util.AppResult
import com.tommy.weatherapp.util.ErrorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData<WeatherUiState>(WeatherUiState.Empty)
    val uiState: LiveData<WeatherUiState> = _uiState

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _searchInlineError = MutableLiveData<String?>(null)
    val searchInlineError: LiveData<String?> = _searchInlineError

    private val _searchSubmitting = MutableLiveData(false)
    val searchSubmitting: LiveData<Boolean> = _searchSubmitting

    val recentSearches: LiveData<List<RecentSearchEntity>> =
        repository.observeRecentSearches().asLiveData()

    fun loadWeatherForCity(city: String, fromSearch: Boolean = false) {
        fetchWeather(
            showFullscreenLoading = _uiState.value !is WeatherUiState.Success,
            fromSearch = fromSearch,
            block = { repository.getWeather(city) },
        )
    }

    fun loadWeatherForCoordinates(lat: Double, lng: Double) {
        fetchWeather(
            showFullscreenLoading = _uiState.value !is WeatherUiState.Success,
            fromSearch = false,
            block = { repository.getWeatherForCoordinates(lat, lng) },
        )
    }

    fun refreshLastKnownCity(lastCity: String?) {
        if (lastCity.isNullOrBlank()) return
        _isRefreshing.value = true
        fetchWeather(
            showFullscreenLoading = false,
            fromSearch = false,
            block = { repository.getWeather(lastCity) },
        )
    }

    fun clearSearchError() {
        _searchInlineError.value = null
    }

    private fun fetchWeather(
        showFullscreenLoading: Boolean,
        fromSearch: Boolean,
        block: suspend () -> AppResult<*>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (showFullscreenLoading) {
                _uiState.postValue(WeatherUiState.Loading)
            }
            if (fromSearch) {
                _searchSubmitting.postValue(true)
                _searchInlineError.postValue(null)
            }
            when (val result = block()) {
                is AppResult.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val data = result.data as com.tommy.weatherapp.domain.model.WeatherDisplayModel
                    _uiState.postValue(WeatherUiState.Success(data))
                    _searchInlineError.postValue(null)
                }

                is AppResult.Error -> {
                    if (fromSearch || result.errorType == ErrorType.INVALID_CITY) {
                        _searchInlineError.postValue(result.message)
                    }
                    _uiState.postValue(
                        if (_uiState.value is WeatherUiState.Success) _uiState.value
                            ?: WeatherUiState.Error(result.message)
                        else WeatherUiState.Error(result.message)
                    )
                }

                AppResult.Loading -> _uiState.postValue(WeatherUiState.Loading)
            }
            _isRefreshing.postValue(false)
            _searchSubmitting.postValue(false)
        }
    }

    class Factory(
        private val repository: WeatherRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
