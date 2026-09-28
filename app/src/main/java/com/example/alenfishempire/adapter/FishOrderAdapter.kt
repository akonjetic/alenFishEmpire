package com.example.alenfishempire.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alenfishempire.R
import com.example.alenfishempire.database.entities.FishOrderItem
import com.example.alenfishempire.databinding.RowFishOrderBinding

class FishOrderAdapter(
    private val orderList: MutableList<FishOrderItem>,
    private val fishNames: List<String>,
    private val fishPriceMap: Map<String, Float>,
    private val onDelete: (Int) -> Unit,
    private val updateTotalCallback: () -> Unit
) : RecyclerView.Adapter<FishOrderAdapter.FishOrderViewHolder>() {

    inner class FishOrderViewHolder(
        val binding: RowFishOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        var quantityWatcher: TextWatcher? = null
        var spinnerListener: AdapterView.OnItemSelectedListener? = null
        var freeListener: android.widget.CompoundButton.OnCheckedChangeListener? = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FishOrderViewHolder {
        val binding = RowFishOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FishOrderViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: FishOrderViewHolder,
        position: Int
    ) {
        val orderItem = orderList[position]
        val context = holder.itemView.context

        // ---------------------------------------------------------
        // Remove old listeners before changing ViewHolder values
        // ---------------------------------------------------------

        holder.quantityWatcher?.let {
            holder.binding.etQuantity.removeTextChangedListener(it)
        }

        holder.spinnerListener?.let {
            holder.binding.spinnerFishType.onItemSelectedListener = null
        }

        holder.binding.cbFree.setOnCheckedChangeListener(null)

        // ---------------------------------------------------------
        // Fish spinner
        // ---------------------------------------------------------

        val spinnerAdapter = ArrayAdapter(
            context,
            R.layout.spinner_dropdown_item,
            fishNames
        )

        spinnerAdapter.setDropDownViewResource(
            R.layout.spinner_dropdown_item
        )

        holder.binding.spinnerFishType.adapter = spinnerAdapter

        val fishIndex = fishNames.indexOf(orderItem.fishType)

        if (fishIndex >= 0) {
            holder.binding.spinnerFishType.setSelection(
                fishIndex,
                false
            )
        }

        val basePrice = fishPriceMap[orderItem.fishType] ?: 0f

        holder.binding.tvPrice.text =
            "€${String.format("%.2f", basePrice)}"

        val spinnerListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    if (pos !in fishNames.indices) return

                    val selectedFish = fishNames[pos]

                    orderItem.fishType = selectedFish
                    orderItem.price =
                        fishPriceMap[selectedFish] ?: 0f

                    holder.binding.tvPrice.text =
                        "€${String.format("%.2f", orderItem.price)}"

                    updateTotalCallback()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        holder.spinnerListener = spinnerListener

        holder.binding.spinnerFishType.onItemSelectedListener =
            spinnerListener

        // ---------------------------------------------------------
        // Quantity
        // ---------------------------------------------------------

        holder.binding.etQuantity.setText(
            orderItem.quantity.toString()
        )

        val quantityWatcher =
            object : TextWatcher {

                override fun afterTextChanged(
                    s: Editable?
                ) {
                    val qty =
                        s?.toString()?.toIntOrNull() ?: 0

                    orderItem.quantity = qty

                    updateTotalCallback()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            }

        holder.quantityWatcher = quantityWatcher

        holder.binding.etQuantity.addTextChangedListener(
            quantityWatcher
        )

        // ---------------------------------------------------------
        // Free checkbox
        // ---------------------------------------------------------

        holder.binding.cbFree.isChecked = orderItem.isFree

        val freeListener =
            android.widget.CompoundButton.OnCheckedChangeListener {
                    _, isChecked ->

                orderItem.isFree = isChecked

                updateTotalCallback()
            }

        holder.freeListener = freeListener

        holder.binding.cbFree.setOnCheckedChangeListener(
            freeListener
        )

        // ---------------------------------------------------------
        // Delete
        // ---------------------------------------------------------

        holder.binding.btnDeleteRow.visibility =
            if (position == 0) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }

        holder.binding.btnDeleteRow.setOnClickListener {
            val currentPosition = orderList.indexOf(orderItem)

            if (currentPosition != -1) {
                onDelete(currentPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}