package com.tommy.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tommy.weatherapp.data.local.entity.RecentSearchEntity
import com.tommy.weatherapp.databinding.ItemRecentSearchBinding

class RecentSearchAdapter(
    private val onClick: (RecentSearchEntity) -> Unit,
) : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {
    private val items = mutableListOf<RecentSearchEntity>()

    fun submitList(data: List<RecentSearchEntity>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding = ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class RecentSearchViewHolder(
        private val binding: ItemRecentSearchBinding,
        private val onClick: (RecentSearchEntity) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentSearchEntity) {
            binding.recentSearchText.text = item.cityName
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
