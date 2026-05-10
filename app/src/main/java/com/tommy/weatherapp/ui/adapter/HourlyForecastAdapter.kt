package com.tommy.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tommy.weatherapp.databinding.ItemHourlyBinding
import com.tommy.weatherapp.domain.model.HourlyForecastItem

class HourlyForecastAdapter : RecyclerView.Adapter<HourlyForecastAdapter.HourlyViewHolder>() {
    private val items = mutableListOf<HourlyForecastItem>()

    fun submitList(data: List<HourlyForecastItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val binding = ItemHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class HourlyViewHolder(
        private val binding: ItemHourlyBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HourlyForecastItem) {
            binding.hourText.text = item.timeLabel
            binding.hourTempText.text = item.temperature
            binding.hourIcon.load(item.iconUrl)
        }
    }
}
