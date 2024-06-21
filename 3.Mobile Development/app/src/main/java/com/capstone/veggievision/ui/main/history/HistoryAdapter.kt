package com.capstone.veggievision.ui.main.history

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.veggievision.R
import com.capstone.veggievision.data.remote.response.ItemHistory
import com.capstone.veggievision.databinding.ItemScanBinding
import com.dicoding.newsapp.utils.DateFormatter
import java.util.TimeZone

class HistoryAdapter(private val historyList: List<ItemHistory>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyItem = historyList[position]
        holder.bind(historyItem)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    class ViewHolder(private val binding: ItemScanBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: ItemHistory) {
            binding.apply {
                itemTitle.text = item.title
                itemDate.text = DateFormatter.formatDateScan(item.timestamp, TimeZone.getDefault().id)
                itemDesc.text = item.desc
                itemPercent.text = "${item.progress}%"

                Glide.with(itemView)
                    .load(item.image)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error)
                    )
                    .into(itemImg)
            }
        }
    }
}