package com.example.alenfishempire.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alenfishempire.R
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.Order
import com.example.alenfishempire.databinding.OrderHistoryItemBinding
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat

class OrderHistoryAdapter(
    private val context: Context,
    private val orders: ArrayList<Map<String, Any>>
): RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = OrderHistoryItemBinding.bind(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newFishOrderDetails: ArrayList<Map<String, Any>>) {
        orders.clear()
        orders.addAll(newFishOrderDetails)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.order_history_item, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val orderMap = orders[position]
        val order = orderMap["order"] as Order
        val fishOrders = orderMap["fishOrders"] as ArrayList<FishOrder>
        val fish = orderMap["fishes"] as ArrayList<Fish>

        val currentItemBinding = holder.binding
        currentItemBinding.orderId.text = order.id.toString()

        var totalQuantity = 0
        var totalPrice = 0.0
        fishOrders.forEach { item ->
            totalQuantity += item.quantity
            totalPrice += (if(item.isFree == true) 0.0 else (fish.find { f -> f.id == item.fishId }?.price?.times(item.quantity.toFloat()) ?: 0.0)).toFloat()
        }

        currentItemBinding.orderDate.text =  SimpleDateFormat("dd-MM-yyyy").format(order.date).toString()
        currentItemBinding.fishQuantity.text = totalQuantity.toString()
        currentItemBinding.totalPrice.text = totalPrice.toString()

        /*TODO dodaj da na klik ode na summary te narudzbe, omoguci da ima export u pdf*/
        /*TODO dodaj da na slide da se obrise narudzba*/
        /*TODO dodaj ljepsi malo dizajn kartice*/

    }
}