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
import androidx.recyclerview.widget.GridLayoutManager
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
import com.tommy.weatherapp.ui.adapter.DailyForecastAdapter
import com.tommy.weatherapp.ui.adapter.HourlyForecastAdapter
import com.tommy.weatherapp.ui.adapter.WeatherDetailAdapter
import com.tommy.weatherapp.ui.state.WeatherUiState
import com.tommy.weatherapp.ui.viewmodel.WeatherViewModel
import com.tommy.weatherapp.util.BackgroundState

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.Factory((requireActivity() as MainActivity).appContainer.weatherRepository)
    }

    private val hourlyAdapter = HourlyForecastAdapter()
    private val dailyAdapter = DailyForecastAdapter()
    private val detailAdapter = WeatherDetailAdapter()

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

        binding.detailRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.detailRecycler.adapter = detailAdapter

        binding.dailyRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.dailyRecycler.adapter = dailyAdapter
    }

    private fun setupActions() {
        binding.searchButton.setOnClickListener {
            (requireActivity() as MainActivity).openSearch()
        }
        binding.emptySearchButton.setOnClickListener {
            (requireActivity() as MainActivity).openSearch()
        }
        binding.retryButton.setOnClickListener {
            when {
                lastRequestedCity != null -> viewModel.loadWeatherForCity(lastRequestedCity!!)
                hasLocationPermission() -> requestCurrentLocation()
                else -> checkLocationPermissionAndLoad()
            }
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
        binding.loadingContainer.visibility = View.GONE
        binding.emptyContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
        binding.swipeRefresh.visibility = View.VISIBLE

        binding.locationText.text = data.locationName
        binding.regionText.text = data.region
        binding.temperatureText.text = data.currentTemperature
        binding.conditionText.text = data.conditionText
        binding.highLowText.text = data.highLow
        binding.conditionIcon.load(data.conditionIconUrl)
        binding.offlineBanner.text = data.offlineBannerText
        binding.offlineBanner.visibility = if (data.offlineBannerText.isNullOrBlank()) View.GONE else View.VISIBLE

        hourlyAdapter.submitList(data.hourlyItems)
        detailAdapter.submitList(data.detailItems)
        dailyAdapter.submitList(data.dailyItems)
        applyBackground(data.backgroundState)
        lastRequestedCity = data.locationName
    }

    private fun applyBackground(state: BackgroundState) {
        val backgroundRes = when (state) {
            BackgroundState.SUNNY -> R.drawable.bg_weather_sunny
            BackgroundState.CLOUDY -> R.drawable.bg_weather_cloudy
            BackgroundState.RAINY -> R.drawable.bg_weather_rainy
            BackgroundState.NIGHT -> R.drawable.bg_weather_night
        }
        binding.weatherRoot.setBackgroundResource(backgroundRes)
    }

    private fun showLoadingState() {
        binding.swipeRefresh.visibility = View.GONE
        binding.emptyContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
        binding.loadingContainer.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.swipeRefresh.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
        binding.loadingContainer.visibility = View.GONE
        binding.emptyContainer.visibility = View.VISIBLE
    }

    private fun showErrorState(message: String) {
        binding.swipeRefresh.visibility = View.GONE
        binding.emptyContainer.visibility = View.GONE
        binding.loadingContainer.visibility = View.GONE
        binding.errorContainer.visibility = View.VISIBLE
        binding.searchButton.visibility = View.VISIBLE
        binding.errorMessage.text = message
    }

    private fun checkLocationPermissionAndLoad() {
        if (hasLocationPermission()) {
            requestCurrentLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestCurrentLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val cancellationTokenSource = CancellationTokenSource()
        locationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            cancellationTokenSource.token,
        ).addOnSuccessListener { location ->
            if (location != null) {
                viewModel.loadWeatherForCoordinates(location.latitude, location.longitude)
            } else {
                showEmptyState()
            }
        }.addOnFailureListener {
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
}
