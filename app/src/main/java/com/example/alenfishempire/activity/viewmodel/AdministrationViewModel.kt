package com.example.alenfishempire.activity.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alenfishempire.database.FishDatabase
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishDTO
import com.example.alenfishempire.database.entities.FishOrder
import kotlinx.coroutines.launch

class AdministrationViewModel : ViewModel() {

    val listOfAllFish = MutableLiveData<ArrayList<Fish>>()

    fun fetchAllFish(context: Context){
        viewModelScope.launch {
            listOfAllFish.value = FishDatabase.getDatabase(context)?.getFishDao()?.getAllFish() as ArrayList<Fish>
        }
    }

    fun saveNewFish(context: Context, fish: Fish){
        viewModelScope.launch {
            val orderId = FishDatabase.getDatabase(context)?.getFishDao()?.insertFish(fish)
        }
    }

    fun updateFish(context: Context, fish: Fish){
        viewModelScope.launch {
            FishDatabase.getDatabase(context)?.getFishDao()?.updateFish(fish)
        }
    }

    fun deleteFish(context: Context, fish: Fish){
        viewModelScope.launch {
            FishDatabase.getDatabase(context)?.getFishDao()?.deleteFish(fish)
        }
    }

}