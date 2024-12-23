package com.example.alenfishempire.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.Order

@Dao
interface FishDao {
    @Query("SELECT * FROM Fish")
    fun getAllFish(): List<Fish>

    @Insert
    suspend fun insertFishOrder(fishOrder: FishOrder)

    @Insert
    suspend fun insertOrder(order: Order)
}