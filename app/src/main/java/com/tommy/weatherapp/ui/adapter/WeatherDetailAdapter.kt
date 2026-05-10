package com.tommy.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tommy.weatherapp.databinding.ItemDetailBinding
import com.tommy.weatherapp.domain.model.WeatherDetailItem

class WeatherDetailAdapter : RecyclerView.Adapter<WeatherDetailAdapter.DetailViewHolder>() {
    private val items = mutableListOf<WeatherDetailItem>()

    fun submitList(data: List<WeatherDetailItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class DetailViewHolder(
        private val binding: ItemDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WeatherDetailItem) {
            binding.detailLabelText.text = item.label
            binding.detailValueText.text = item.value
        }
    }
}
