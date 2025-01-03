package com.example.alenfishempire.activity.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alenfishempire.database.FishDatabase
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.Serializable

class OrderHistoryViewModel : ViewModel() {

    fun fetchAllOrdersWithDetails(context: Context, callback: (List<Map<String, Any>>) -> Unit) {
        viewModelScope.launch {
            // Dohvati sve narudžbe (Order)
            val allOrders = FishDatabase.getDatabase(context)?.getFishDao()?.getAllOrdersDesc() ?: emptyList()

            val detailsList = mutableListOf<Map<String, Any>>()

            // Za svaku narudžbu (Order), dohvatimo FishOrder i Fish entitete
            allOrders.forEach { order ->
                val fishOrderList = mutableListOf<FishOrder>()
                val fishList = mutableListOf<Fish>()

                // Za svaki fishOrderId u fishOrderId listi narudžbe, dohvatimo FishOrder i Fish
                order.fishOrderId.forEach { fishOrderId ->
                    // Dohvatimo FishOrder entitet
                    val fishOrderDeferred = async {
                        FishDatabase.getDatabase(context)?.getFishDao()?.getFishOrderById(fishOrderId)
                    }
                    val fishDeferred = async {
                        val fishOrder = fishOrderDeferred.await()
                        fishOrder?.let {
                            FishDatabase.getDatabase(context)?.getFishDao()?.getFishById(it.fishId)
                        }
                    }

                    // Pričekaj sve async pozive
                    val fishOrder = fishOrderDeferred.await()
                    val fish = fishDeferred.await()

                    fishOrder?.let { fishOrderList.add(it) }
                    fish?.let { fishList.add(it) }
                }

                // Sastavljamo mapu za trenutnu narudžbu
                val detailsMap = mapOf(
                    "order" to order,
                    "fishOrders" to fishOrderList,
                    "fishes" to fishList
                )

                detailsList.add(detailsMap)
            }

            // Kada su svi podaci dohvaćeni, šaljemo ih natrag putem callbacka
            callback(detailsList)
        }
    }
}