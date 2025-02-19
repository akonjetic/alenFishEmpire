package com.example.alenfishempire.activity.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alenfishempire.database.FishDatabase
import com.example.alenfishempire.database.entities.FishOrderDetail
import com.example.alenfishempire.database.entities.OrderWithDetails
import com.example.alenfishempire.database.entities.OrderWithDetailsRaw
import kotlinx.coroutines.launch

class OrderHistoryViewModel : ViewModel() {

    fun filterOrdersByDateRange(
        context: Context,
        startDate: String?,
        endDate: String?,
        sortBy: String,
        callback: (ArrayList<OrderWithDetails>) -> Unit
    ) {
        viewModelScope.launch {
            val rawOrders = FishDatabase.getDatabase(context)?.getFishDao()
                ?.getFilteredAndSortedOrders(startDate, endDate, sortBy)
            val processedOrders = rawOrders?.let { convertRawToOrderWithDetails(it) } ?: emptyList()
            callback(ArrayList(processedOrders))
        }
    }


    fun getAllOrders(context: Context) {
        viewModelScope.launch {
            val orders = FishDatabase.getDatabase(context)?.getFishDao()?.getAllOrders()
            Log.d("OrderHistory", "Orders found: ${orders?.size}")
        }
    }


    fun convertRawToOrderWithDetails(rawOrders: List<OrderWithDetailsRaw>): List<OrderWithDetails> {
        return rawOrders.map { raw ->
            val fishList = raw.fishDetails?.split(";")?.mapNotNull { fishDetail ->
                val parts = fishDetail.split(":")
                if (parts.size == 3) {
                    FishOrderDetail(
                        fishName = parts[0],
                        quantity = parts[1].toIntOrNull() ?: 0,
                        price = parts[2].toFloatOrNull() ?: 0f
                    )
                } else null
            } ?: emptyList()

            OrderWithDetails(
                id = raw.id,
                date = raw.date,
                fishOrderList = fishList,
                discount = raw.discount,
                totalPrice = raw.totalPrice,
                totalQuantity = raw.totalQuantity
            )
        }
    }

}