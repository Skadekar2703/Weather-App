package com.tommy.weatherapp.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.tommy.weatherapp.MainActivity
import com.tommy.weatherapp.R
import com.tommy.weatherapp.databinding.FragmentWeatherBinding
import com.tommy.weatherapp.domain.model.WeatherDisplayModel
import com.tommy.weatherapp.ui.adapter.HourlyForecastAdapter
import com.tommy.weatherapp.ui.state.WeatherUiState
import com.tommy.weatherapp.ui.viewmodel.WeatherViewModel
import com.tommy.weatherapp.util.BackgroundState
import com.tommy.weatherapp.util.WeatherFormatters

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.Factory((requireActivity() as MainActivity).appContainer.weatherRepository)
    }

    private val hourlyAdapter = HourlyForecastAdapter()

    private var lastRequestedCity: String? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            requestCurrentLocation()
        } else {
            val permanentlyDenied = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            if (permanentlyDenied) {
                Snackbar.make(binding.root, R.string.permission_denied_permanently, Snackbar.LENGTH_LONG)
                    .setAction(R.string.open_settings) { openSettings() }
                    .show()
            }
            showEmptyState()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupActions()
        observeViewModel()

        if (savedInstanceState == null) {
            checkLocationPermissionAndLoad()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViews() {
        binding.hourlyRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.hourlyRecycler.adapter = hourlyAdapter
    }

    private fun setupActions() {
        binding.searchButton.setOnClickListener {
            (requireActivity() as MainActivity).openSearch()
        }
        binding.emptySearchButton.setOnClickListener {
            (requireActivity() as MainActivity).openSearch()
        }
        binding.allowAccessButton.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        binding.retryButton.setOnClickListener {
            when {
                lastRequestedCity != null -> viewModel.loadWeatherForCity(lastRequestedCity!!)
                hasLocationPermission() -> requestCurrentLocation()
                else -> showEmptyState()
            }
        }
        binding.checkSettingsButton.setOnClickListener { openSettings() }
        binding.hourlySectionAction.setOnClickListener {
            (requireActivity() as MainActivity).openForecastDetail()
        }
        binding.swipeRefresh.setOnRefreshListener {
            if (lastRequestedCity != null) {
                viewModel.refreshLastKnownCity(lastRequestedCity)
            } else if (hasLocationPermission()) {
                requestCurrentLocation()
            } else {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                WeatherUiState.Empty -> showEmptyState()
                is WeatherUiState.Error -> showErrorState(state.message)
                WeatherUiState.Loading -> showLoadingState()
                is WeatherUiState.Success -> renderWeather(state.data)
            }
        }
        viewModel.isRefreshing.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it
        }
    }

    private fun renderWeather(data: WeatherDisplayModel) {
        val binding = _binding ?: return
        binding.loadingContainer.visibility = View.GONE
        binding.emptyContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
        binding.swipeRefresh.visibility = View.VISIBLE

        binding.topLocationText.text = data.locationName
        binding.heroLocationText.text = if (data.backgroundState == BackgroundState.SUNNY) "" else data.locationName
        binding.regionText.text = data.region
        binding.temperatureText.text = data.currentTemperature
        binding.conditionText.text = data.conditionText
        val parts = data.highLow.split("  ")
        binding.highPillText.text = parts.getOrNull(0) ?: data.highLow
        binding.lowPillText.text = parts.getOrNull(1) ?: ""
        binding.conditionIcon.load(data.conditionIconUrl)

        hourlyAdapter.submitList(data.hourlyItems)
        applyBackground(data.backgroundState)
        bindMetricCards(data)
        bindFeatureCard(data)
        lastRequestedCity = data.locationName
    }

    private fun applyBackground(state: BackgroundState) {
        val binding = _binding ?: return
        val backgroundRes = when (state) {
            BackgroundState.SUNNY -> R.drawable.bg_weather_sunny
            BackgroundState.CLOUDY -> R.drawable.bg_weather_cloudy
            BackgroundState.RAINY -> R.drawable.bg_weather_rainy
            BackgroundState.NIGHT -> R.drawable.bg_weather_night
        }
        binding.weatherRoot.setBackgroundResource(backgroundRes)
    }

    private fun showLoadingState() {
        val binding = _binding ?: return
        binding.swipeRefresh.visibility = View.INVISIBLE
        binding.emptyContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
        binding.loadingContainer.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        val binding = _binding ?: return
        binding.swipeRefresh.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
        binding.loadingContainer.visibility = View.GONE
        binding.emptyContainer.visibility = View.VISIBLE
        binding.topLocationText.text = ""
    }

    private fun showErrorState(message: String) {
        val binding = _binding ?: return
        binding.swipeRefresh.visibility = View.GONE
        binding.emptyContainer.visibility = View.GONE
        binding.loadingContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.VISIBLE
        binding.errorMessage.text = message
    }

    private fun checkLocationPermissionAndLoad() {
        if (hasLocationPermission()) {
            requestCurrentLocation()
        } else {
            showEmptyState()
        }
    }

    private fun requestCurrentLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val cancellationTokenSource = CancellationTokenSource()
        locationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            cancellationTokenSource.token,
        ).addOnSuccessListener { location ->
            if (!isAdded || _binding == null) return@addOnSuccessListener
            if (location != null) {
                viewModel.loadWeatherForCoordinates(location.latitude, location.longitude)
            } else {
                showEmptyState()
            }
        }.addOnFailureListener {
            if (!isAdded || _binding == null) return@addOnFailureListener
            showEmptyState()
        }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    private fun openSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", requireContext().packageName, null),
            )
        )
    }

    private fun bindMetricCards(data: WeatherDisplayModel) {
        val binding = _binding ?: return
        val details = data.detailItems.associateBy { it.label.lowercase() }
        val cards = when (data.backgroundState) {
            BackgroundState.SUNNY -> listOf(
                Triple("Humidity", details["humidity"]?.value ?: "--", "The dew point is ${details["humidity"]?.value ?: "--"} right now."),
                Triple("Wind", details["wind"]?.value ?: "--", "Direction based on the latest gust data."),
                Triple("UV Index", details["uv"]?.value ?: "--", "High risk of harm from unprotected sun exposure."),
                Triple("Precipitation", "0mm in last 24h", "Live weather map overlay."),
            )
            BackgroundState.RAINY -> listOf(
                Triple("Humidity", details["humidity"]?.value ?: "--", "High moisture level"),
                Triple("Wind", details["wind"]?.value ?: "--", "Breeze from North"),
                Triple("Precipitation", "92% Chance", "Rain expected through the afternoon."),
                Triple("Visibility", details["visibility"]?.value ?: "--", "Wet roads and low skyline contrast."),
            )
            BackgroundState.NIGHT -> listOf(
                Triple("UV Index", details["uv"]?.value ?: "0", "None"),
                Triple("Humidity", details["humidity"]?.value ?: "--", "Dew point is 9°"),
                Triple("Visibility", details["visibility"]?.value ?: "--", "Clear view ahead"),
                Triple("Pressure", "1012 hPa", "Steady"),
            )
            BackgroundState.CLOUDY -> listOf(
                Triple("Feels Like", details["feels like"]?.value ?: "--", "Current comfort level"),
                Triple("Humidity", details["humidity"]?.value ?: "--", "Moisture across the region"),
                Triple("Wind", details["wind"]?.value ?: "--", "Mild coastal breeze"),
                Triple("Visibility", details["visibility"]?.value ?: "--", "Cloud layers thinning later"),
            )
        }
        bindMetric(binding.metricOneLabel, binding.metricOneValue, binding.metricOneSubtitle, cards[0])
        bindMetric(binding.metricTwoLabel, binding.metricTwoValue, binding.metricTwoSubtitle, cards[1])
        bindMetric(binding.metricThreeLabel, binding.metricThreeValue, binding.metricThreeSubtitle, cards[2])
        bindMetric(binding.metricFourLabel, binding.metricFourValue, binding.metricFourSubtitle, cards[3])
        binding.metricFourImage.load(
            when (data.backgroundState) {
                BackgroundState.SUNNY -> MAP_IMAGE_URL
                BackgroundState.RAINY -> RAIN_DETAIL_IMAGE_URL
                BackgroundState.NIGHT -> AIR_QUALITY_IMAGE_URL
                BackgroundState.CLOUDY -> MAP_IMAGE_URL
            }
        )
    }

    private fun bindMetric(
        labelView: android.widget.TextView,
        valueView: android.widget.TextView,
        subtitleView: android.widget.TextView,
        payload: Triple<String, String, String>,
    ) {
        labelView.text = payload.first
        valueView.text = payload.second
        subtitleView.text = payload.third
    }

    private fun bindFeatureCard(data: WeatherDisplayModel) {
        val binding = _binding ?: return
        when (data.backgroundState) {
            BackgroundState.SUNNY -> {
                binding.featureTitle.text = getString(R.string.atmospheric_outlook)
                binding.featureValue.text = "24°"
                binding.featureSubtitle.text = "Bright conditions continue through the afternoon."
                binding.featureImage.load(SUNNY_FEATURE_IMAGE_URL)
            }
            BackgroundState.RAINY -> {
                binding.featureTitle.text = "Visibility"
                binding.featureValue.text = detailsValue(data, "visibility", "4.2 km")
                binding.featureSubtitle.text = "Storm cells will keep streets reflective and dim."
                binding.featureImage.load(RAIN_FEATURE_IMAGE_URL)
            }
            BackgroundState.NIGHT -> {
                binding.featureTitle.text = "Air Quality"
                binding.featureValue.text = "24 AQI"
                binding.featureSubtitle.text = "Good"
                binding.featureImage.load(AIR_QUALITY_IMAGE_URL)
            }
            BackgroundState.CLOUDY -> {
                binding.featureTitle.text = getString(R.string.atmospheric_outlook)
                binding.featureValue.text = WeatherFormatters.formatTemperature(18.0)
                binding.featureSubtitle.text = "Cloud bands should open into a calmer evening."
                binding.featureImage.load(OUTLOOK_IMAGE_URL)
            }
        }
    }

    private fun detailsValue(data: WeatherDisplayModel, key: String, fallback: String): String =
        data.detailItems.firstOrNull { it.label.equals(key, ignoreCase = true) }?.value ?: fallback

    private companion object {
        const val MAP_IMAGE_URL =
            "https://lh3.googleusercontent.com/aida-public/AB6AXuBNO2wlwXvtUW3tmyr6kcg0cZ4WgTHowJeOn7xRGiKc5J4tv-qi4Lvy8ojbC6kl6n0j5xnyDmAn3C2cU3Nv6BNQtW1JyDBA2UcT_8XKYXjS_fiYZZpYj54kLm5OPInVgx005OqdgLVJ9w5fe86D0BFfAw2FkJ0ZmlrxLMVh_UJVrpcWWp5r96Jylu0KP0svaPtl6i8oyffkImnr2OCRk2kXKi7IbYGHICzKXq2aX1kQB-JH6iuHQKPMqYM1p5s_cC55PQO_IK_ujY-K"
        const val SUNNY_FEATURE_IMAGE_URL = MAP_IMAGE_URL
        const val RAIN_FEATURE_IMAGE_URL =
            "https://images.unsplash.com/photo-1500375592092-40eb2168fd21?auto=format&fit=crop&w=1200&q=80"
        const val RAIN_DETAIL_IMAGE_URL =
            "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?auto=format&fit=crop&w=900&q=80"
        const val AIR_QUALITY_IMAGE_URL =
            "https://images.unsplash.com/photo-1519904981063-b0cf448d479e?auto=format&fit=crop&w=1200&q=80"
        const val OUTLOOK_IMAGE_URL =
            "https://images.unsplash.com/photo-1501630834273-4b5604d2ee31?auto=format&fit=crop&w=1200&q=80"
    }
}
