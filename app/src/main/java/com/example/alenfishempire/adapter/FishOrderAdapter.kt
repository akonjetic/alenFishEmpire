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
    private val updateTotalCallback: () -> Unit // Dodajemo callback za ažuriranje totalne cijene u Activityju
) : RecyclerView.Adapter<FishOrderAdapter.FishOrderViewHolder>() {

    inner class FishOrderViewHolder(val binding: RowFishOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishOrderViewHolder {
        val binding = RowFishOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FishOrderViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FishOrderViewHolder, position: Int) {
        val orderItem = orderList[position]
        val context = holder.itemView.context

        // Postavi adapter za Spinner
        val adapter = ArrayAdapter(context, R.layout.spinner_dropdown_item, fishNames)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        holder.binding.spinnerFishType.adapter = adapter

        // Odaberi odgovarajući item u spinneru
        holder.binding.spinnerFishType.setSelection(fishNames.indexOf(orderItem.fishType), false)

        // Postavi početnu cijenu (cijena komada iz baze)
        val basePrice = fishPriceMap[orderItem.fishType] ?: 0f
        holder.binding.tvPrice.text = "€${String.format("%.2f", basePrice)}"

        // Spinner promjena ribe
        holder.binding.spinnerFishType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selectedFish = fishNames[pos]
                orderItem.fishType = selectedFish
                orderItem.price = fishPriceMap[selectedFish] ?: 0f

                // **Postavimo cijenu**
                holder.binding.tvPrice.text = "€${String.format("%.2f", orderItem.price)}"

                // **Ažuriramo ukupnu cijenu u glavnom XML-u**
                updateTotalCallback()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Postavljanje quantity-a i listenera
        holder.binding.etQuantity.setText(orderItem.quantity.toString())
        holder.binding.etQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val qty = s.toString().toIntOrNull() ?: 0
                orderItem.quantity = qty

                // **Pozivamo updateTotalCallback kako bi se ažurirala ukupna cijena u glavnom XML-u**
                updateTotalCallback()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // CheckBox Free event
        holder.binding.cbFree.setOnCheckedChangeListener { _, isChecked ->
            orderItem.isFree = isChecked
            updateTotalCallback() // Ažuriramo ukupnu cijenu
        }

        // Sakrij delete ikonu ako je prvi red
        holder.binding.btnDeleteRow.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE

        // Brisanje reda
        holder.binding.btnDeleteRow.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount(): Int = orderList.size
}
