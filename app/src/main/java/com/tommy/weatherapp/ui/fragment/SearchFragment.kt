package com.tommy.weatherapp.ui.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tommy.weatherapp.MainActivity
import com.tommy.weatherapp.databinding.FragmentSearchBinding
import com.tommy.weatherapp.ui.adapter.RecentSearchAdapter
import com.tommy.weatherapp.ui.state.WeatherUiState
import com.tommy.weatherapp.ui.viewmodel.WeatherViewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.Factory((requireActivity() as MainActivity).appContainer.weatherRepository)
    }

    private val recentSearchAdapter = RecentSearchAdapter {
        binding.searchEditText.setText(it.cityName)
        submitSearch(it.cityName)
    }

    private var awaitingResult = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recentSearchesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recentSearchesRecycler.adapter = recentSearchAdapter

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.searchEditText.setOnEditorActionListener { _, actionId, event ->
            val isSubmit = actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            if (isSubmit) {
                submitSearch(binding.searchEditText.text?.toString().orEmpty())
            }
            isSubmit
        }
        binding.searchEditText.doAfterTextChanged {
            if (!it.isNullOrBlank()) {
                binding.searchErrorText.visibility = View.GONE
                viewModel.clearSearchError()
            }
        }
        binding.clearAllButton.setOnClickListener {
            binding.searchEditText.text?.clear()
        }

        listOf(
            binding.chipLondon,
            binding.chipSeoul,
            binding.chipSydney,
            binding.chipDubai,
            binding.chipBerlin,
        ).forEach { chip ->
            chip.setOnClickListener { submitSearch(chip.text.toString()) }
        }

        viewModel.recentSearches.observe(viewLifecycleOwner) {
            recentSearchAdapter.submitList(it)
        }
        viewModel.searchInlineError.observe(viewLifecycleOwner) {
            binding.searchErrorText.text = it
            binding.searchErrorText.visibility = if (it.isNullOrBlank()) View.GONE else View.VISIBLE
        }
        viewModel.searchSubmitting.observe(viewLifecycleOwner) {
            binding.searchProgress.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            if (awaitingResult && state is WeatherUiState.Success) {
                awaitingResult = false
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun submitSearch(query: String) {
        awaitingResult = true
        viewModel.loadWeatherForCity(query, fromSearch = true)
    }
}
