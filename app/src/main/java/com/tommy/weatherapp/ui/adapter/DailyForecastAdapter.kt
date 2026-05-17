package com.tommy.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tommy.weatherapp.databinding.ItemDailyBinding
import com.tommy.weatherapp.domain.model.DailyForecastItem

class DailyForecastAdapter : RecyclerView.Adapter<DailyForecastAdapter.DailyViewHolder>() {
    private val items = mutableListOf<DailyForecastItem>()

    fun submitList(data: List<DailyForecastItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class DailyViewHolder(
        private val binding: ItemDailyBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DailyForecastItem) {
            binding.dayText.text = item.dayLabel
            binding.dayConditionText.visibility = android.view.View.VISIBLE
            binding.dayConditionText.text = item.conditionText
            binding.dayHighLowText.text = item.highLow
            binding.dayRainText.text = "Rain ${item.rainChance}"
            binding.dayIcon.load(item.iconUrl)
        }
    }
}
