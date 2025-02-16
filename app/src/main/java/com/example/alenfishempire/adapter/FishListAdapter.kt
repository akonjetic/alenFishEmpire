package com.example.alenfishempire.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.alenfishempire.R
import com.example.alenfishempire.activity.fragment.EditFishFragment
import com.example.alenfishempire.adapter.OrderHistoryAdapter.OrderHistoryViewHolder
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.OrderWithDetails
import com.example.alenfishempire.databinding.FishListItemBinding
import com.example.alenfishempire.databinding.OrderHistoryItemBinding

class FishListAdapter(
    private val context: Context,
    private val fish: ArrayList<Fish>,
    private val onFishClick: (Fish) -> Unit
    ): RecyclerView.Adapter<FishListAdapter.FishListViewHolder>() {

    private val fishIcons = listOf(
        R.drawable.ic_aqua_icon1,
        R.drawable.ic_aqua_icon2,
        R.drawable.ic_aqua_icon3,
        R.drawable.ic_aqua_icon4,
        R.drawable.ic_aqua_icon5
    )

    class FishListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FishListItemBinding.bind(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newFish: ArrayList<Fish>) {
        fish.clear()
        fish.addAll(newFish)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fish_list_item, parent, false)
        return FishListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fish.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FishListViewHolder, position: Int) {
        val currentItem = fish[position]

        val currentItemBinding = holder.binding
        currentItemBinding.tvFishName.text = currentItem.name
        currentItemBinding.tvFishPrice.text = "€${currentItem.price}"

        val iconResId = fishIcons[position % fishIcons.size]
        currentItemBinding.ivFishIcon.setImageResource(iconResId)

        holder.itemView.setOnClickListener {
            val fragment = EditFishFragment.newInstance(currentItem)
            if (context is AppCompatActivity) {
                fragment.show(context.supportFragmentManager, "EditFishFragment")
            }
        }

    }

}