package com.example.alenfishempire.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.alenfishempire.R
import com.example.alenfishempire.activity.fragment.EditFishFragment
import com.example.alenfishempire.adapter.FishListAdapter.FishListViewHolder
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishSalesStats
import com.example.alenfishempire.databinding.FishListItemBinding
import com.example.alenfishempire.databinding.StatListItemBinding

class FishStatsAdapter(
    private val context: Context,
    private var stats: ArrayList<FishSalesStats>
) : RecyclerView.Adapter<FishStatsAdapter.FishSalesStatsViewHolder>() {

    class FishSalesStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = StatListItemBinding.bind(view)
    }

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


    override fun onBindViewHolder(holder: FishSalesStatsViewHolder, position: Int) {
        val currentItem = stats[position]

        val currentItemBinding = holder.binding
        currentItemBinding.statTotalSales.text = currentItem.totalSales.toString()
        currentItemBinding.statTotalQuantity.text = currentItem.totalQuantity.toString()
        currentItemBinding.statFishName.text = currentItem.fishName


  }

    override fun getItemCount(): Int = stats.size


}
