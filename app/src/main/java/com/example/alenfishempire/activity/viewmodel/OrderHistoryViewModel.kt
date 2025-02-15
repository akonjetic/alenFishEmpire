package com.example.alenfishempire.activity.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alenfishempire.database.FishDatabase
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.Order
import com.example.alenfishempire.database.entities.OrderWithDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.Serializable

class OrderHistoryViewModel : ViewModel() {

    private val monthFilter = MutableLiveData<String?>()
    private val yearFilter = MutableLiveData<String?>()
    private val sortByFilter = MutableLiveData<String>()

    fun filterByMonth(month: String?) {
        monthFilter.value = month
    }

    fun filterByYear(year: String?) {
        yearFilter.value = year
    }

    fun sortBy(sortOption: String) {
        sortByFilter.value = sortOption
    }

    fun filterOrdersByDateRange(context: Context, startDate: String?, endDate: String?, sortBy: String, callback: (ArrayList<OrderWithDetails>) -> Unit) {
        viewModelScope.launch {
            val filteredOrders = FishDatabase.getDatabase(context)?.getFishDao()?.getFilteredAndSortedOrders(startDate, endDate, sortBy)
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