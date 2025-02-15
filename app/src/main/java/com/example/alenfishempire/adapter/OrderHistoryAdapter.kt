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
import com.example.alenfishempire.database.entities.OrderWithDetails
import com.example.alenfishempire.databinding.OrderHistoryItemBinding
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat

class OrderHistoryAdapter(
    private val context: Context,
    private val orders: ArrayList<OrderWithDetails>
): RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = OrderHistoryItemBinding.bind(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newFishOrderDetails: ArrayList<OrderWithDetails>) {
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
        val orderDetails = orders[position]

        val currentItemBinding = holder.binding
        currentItemBinding.orderId.text = buildString {
            append("Order #")
            append(orderDetails.id.toString())
        }


        currentItemBinding.orderDate.text =  SimpleDateFormat("dd/MM/yyyy").format(orderDetails.date).toString()
        currentItemBinding.fishQuantity.text = buildString {
            append("Quantity: ")
            append(orderDetails.totalQuantity.toString())
        }
        currentItemBinding.totalPrice.text = buildString {
            append("€")
            append(orderDetails.totalPrice.toString())
        }

        /*TODO dodaj da na klik ode na summary te narudzbe, omoguci da ima export u pdf*/

    }
}