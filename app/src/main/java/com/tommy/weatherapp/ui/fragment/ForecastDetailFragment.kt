package com.tommy.weatherapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.tommy.weatherapp.MainActivity
import com.tommy.weatherapp.databinding.FragmentForecastDetailBinding
import com.tommy.weatherapp.ui.adapter.DailyForecastAdapter
import com.tommy.weatherapp.ui.state.WeatherUiState
import com.tommy.weatherapp.ui.viewmodel.WeatherViewModel

class ForecastDetailFragment : Fragment() {
    private var _binding: FragmentForecastDetailBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.Factory((requireActivity() as MainActivity).appContainer.weatherRepository)
    }

    private val dailyAdapter = DailyForecastAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentForecastDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dailyRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.dailyRecycler.adapter = dailyAdapter
        binding.outlookImage.load(OUTLOOK_IMAGE_URL)

        binding.backButton.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.detailSearchButton.setOnClickListener { (requireActivity() as MainActivity).openSearch() }
        binding.detailLocationsButton.setOnClickListener { (requireActivity() as MainActivity).openSearch() }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            if (state is WeatherUiState.Success) {
                dailyAdapter.submitList(state.data.dailyItems)
                binding.outlookSubtitle.text = when {
                    state.data.dailyItems.any { it.rainChance.removeSuffix("%").toIntOrNull() ?: 0 > 70 } ->
                        "High pressure system moving in after the wettest stretch."
                    else -> "Stable skies should hold through the next five days."
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val OUTLOOK_IMAGE_URL =
            "https://images.unsplash.com/photo-1501630834273-4b5604d2ee31?auto=format&fit=crop&w=1200&q=80"
    }
}
