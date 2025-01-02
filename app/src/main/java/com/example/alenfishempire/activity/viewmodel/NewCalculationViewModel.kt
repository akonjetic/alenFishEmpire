package com.example.alenfishempire.activity.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alenfishempire.database.FishDatabase
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.Order
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.Serializable

class NewCalculationViewModel : ViewModel() {

    val listOfAllFish = MutableLiveData<ArrayList<Fish>>()
    val fishOrder = MutableLiveData<FishOrder>()

    private val _fishOrderDetails = MutableLiveData<Map<String, Serializable>>()
    val fishOrderDetails: LiveData<Map<String, Serializable>> get() = _fishOrderDetails

    fun fetchFishOrderDetails(context: Context, fishOrderIds: List<Long>, callback: (List<Map<String, Serializable>>) -> Unit) {
        viewModelScope.launch {
            val detailsList = mutableListOf<Map<String, Serializable>>()

            // Dohvati podatke za svaki fishOrderId
            fishOrderIds.forEach { fishOrderId ->
                val fishOrderDeferred = async {
                    FishDatabase.getDatabase(context)?.getFishDao()?.getFishOrderById(fishOrderId)
                }
                val fishDeferred = async {
                    val fishOrder = fishOrderDeferred.await()
                    FishDatabase.getDatabase(context)?.getFishDao()?.getFishById(fishOrder?.fishId!!)
                }

                val fishOrder = fishOrderDeferred.await()
                val fish = fishDeferred.await()

                val detailsMap = mapOf(
                    "fishOrder" to fishOrder!!,
                    "fish" to fish!!
                )

                detailsList.add(detailsMap)
            }

            // Kada su svi podaci dohvaćeni, šaljemo ih natrag
            callback(detailsList)
        }
    }

    fun fetchAllFish(context: Context){
        viewModelScope.launch {
            listOfAllFish.value = FishDatabase.getDatabase(context)?.getFishDao()?.getAllFish() as ArrayList<Fish>
        }
    }

    fun fetchFishOrderById(context: Context, fishOrderId: Long) {
        viewModelScope.launch {
            fishOrder.value = FishDatabase.getDatabase(context)?.getFishDao()?.getFishOrderById(fishOrderId)
        }
    }

    fun saveNewFishOrder(context: Context, fishOrder: FishOrder, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val orderId = FishDatabase.getDatabase(context)?.getFishDao()?.insertFishOrder(fishOrder)
            callback(orderId!!)
        }
    }

    fun saveNewOrder(context: Context, order: Order, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val orderId = FishDatabase.getDatabase(context)?.getFishDao()?.insertOrder(order)
            callback(orderId!!)
        }
    }



}