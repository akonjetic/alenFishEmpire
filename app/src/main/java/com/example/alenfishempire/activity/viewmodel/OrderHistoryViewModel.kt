package com.example.alenfishempire.activity.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alenfishempire.database.FishDatabase
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
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

    // Funkcija za dohvatanje filtriranih i sortiranih narudžbi
    fun fetchFilteredAndSortedOrders(
        context: Context,
        callback: (ArrayList<OrderWithDetails>) -> Unit
    ) {
        viewModelScope.launch {
        val fishDao = FishDatabase.getDatabase(context)?.getFishDao()

            val dates = fishDao?.getOrdersByMonthAndYear("01", "2025")
            Log.d("OrderHistory", "Fetched dates: $dates")
        // Apply filters
        val selectedMonth = monthFilter.value
        val selectedYear = yearFilter.value
        val selectedSortBy = sortByFilter.value

        // Fetch orders using selected filters and sorting
        val allOrders = fishDao?.getFilteredAndSortedOrders(
            month = selectedMonth,
            year = selectedYear,
            sortBy = selectedSortBy ?: "date_desc" // Default sorting if none provided
        ) ?: emptyList()

        // Log the fetched orders for debugging purposes
        Log.d("OrderHistory", "Fetched orders: $allOrders")

        // Send the fetched orders
        callback(ArrayList(allOrders))
    }
    }



}