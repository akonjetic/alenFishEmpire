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

    override fun onBindViewHolder(holder: FishListViewHolder, position: Int) {
        val currentItem = fish[position]

        val currentItemBinding = holder.binding
        currentItemBinding.tvFishName.text = currentItem.name
        currentItemBinding.tvFishPrice.text = currentItem.price.toString()

        holder.itemView.setOnClickListener {
            val fragment = EditFishFragment.newInstance(currentItem)
            if (context is AppCompatActivity) {
                fragment.show(context.supportFragmentManager, "EditFishFragment")
            }
        }

    }

}