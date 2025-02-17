package com.example.alenfishempire.activity.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alenfishempire.database.FishDatabase
import com.example.alenfishempire.database.entities.OrderWithDetails
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
            val filteredOrders = FishDatabase.getDatabase(context)?.getFishDao()
                ?.getFilteredAndSortedOrders(startDate, endDate, sortBy)
            callback(ArrayList(filteredOrders))
        }
    }

    fun getAllOrders(context: Context) {
        viewModelScope.launch {
            val orders = FishDatabase.getDatabase(context)?.getFishDao()?.getAllOrders()
            Log.d("OrderHistory", "Orders found: ${orders?.size}")
        }
    }


}