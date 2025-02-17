package com.example.alenfishempire.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alenfishempire.R
import com.example.alenfishempire.database.entities.FishSalesStats
import com.example.alenfishempire.databinding.StatListItemBinding

class FishStatsAdapter(
    private val context: Context,
    private var stats: ArrayList<FishSalesStats>
) : RecyclerView.Adapter<FishStatsAdapter.FishSalesStatsViewHolder>() {

    class FishSalesStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = StatListItemBinding.bind(view)
    }

    private val fishIcons = listOf(
        R.drawable.ic_aqua_icon1,
        R.drawable.ic_aqua_icon2,
        R.drawable.ic_aqua_icon3,
        R.drawable.ic_aqua_icon4,
        R.drawable.ic_aqua_icon5
    )

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newStats: ArrayList<FishSalesStats>) {
        stats.clear()
        stats.addAll(newStats)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishSalesStatsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.stat_list_item, parent, false)
        return FishSalesStatsViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FishSalesStatsViewHolder, position: Int) {
        val currentItem = stats[position]

        val currentItemBinding = holder.binding
        currentItemBinding.statTotalSales.text = "Total: €${String.format("%.2f", currentItem.totalSales)}"
        currentItemBinding.statTotalQuantity.text = "Qty: ${currentItem.totalQuantity}"
        currentItemBinding.statFishName.text = currentItem.fishName

        val iconResId = fishIcons[position % fishIcons.size]
        currentItemBinding.statFishIcon.setImageResource(iconResId)

    }

    override fun getItemCount(): Int = stats.size


}
