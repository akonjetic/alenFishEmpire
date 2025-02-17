package com.example.alenfishempire.activity.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alenfishempire.database.FishDatabase
import com.example.alenfishempire.database.entities.FishSalesStats
import kotlinx.coroutines.launch

class StatsViewModel : ViewModel() {

    val listOfAllFishSalesStats = MutableLiveData<List<FishSalesStats>>()
    val totalSales = MutableLiveData<Float>()
    val totalFreeFish = MutableLiveData<Int>()

    fun fetchAllFishSalesStats(context: Context) {
        viewModelScope.launch {
            listOfAllFishSalesStats.value =
                FishDatabase.getDatabase(context)?.getFishDao()?.getTotalFishSalesByType()

        }
    }

    fun fetchTotalSales(context: Context) {
        viewModelScope.launch {
            totalSales.value = FishDatabase.getDatabase(context)?.getFishDao()?.getTotalSales()
        }
    }

    fun fetchTotalFreeFish(context: Context) {
        viewModelScope.launch {
            totalFreeFish.value =
                FishDatabase.getDatabase(context)?.getFishDao()?.getTotalFreeFish()
        }
    }


}