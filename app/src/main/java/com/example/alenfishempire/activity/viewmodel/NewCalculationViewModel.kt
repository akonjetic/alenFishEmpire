package com.example.alenfishempire.activity.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alenfishempire.database.FishDatabase
import com.example.alenfishempire.database.entities.Fish
import kotlinx.coroutines.launch

class NewCalculationViewModel : ViewModel() {

    val listOfAllFish = MutableLiveData<ArrayList<Fish>>()

    fun fetchAllFish(context: Context){
        viewModelScope.launch {
            listOfAllFish.value = FishDatabase.getDatabase(context)?.getFishDao()?.getAllFish() as ArrayList<Fish>
        }
    }
}