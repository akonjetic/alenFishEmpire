package com.example.alenfishempire.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.Order

@Dao
interface FishDao {
    @Query("SELECT * FROM Fish")
    suspend fun getAllFish(): List<Fish>

    @Query("SELECT * FROM FishOrder Where fishOrderId = :id")
    suspend fun getFishOrderById(id : Long): FishOrder

    @Query("SELECT * FROM Fish Where fishId = :id")
    suspend fun getFishById(id : Long): Fish

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFish(fish: Fish)

    @Query("Select * from 'Order' Order By orderId DESC")
    suspend fun getAllOrdersDesc() : List<Order>

    @Insert
    suspend fun insertFishOrder(fishOrder: FishOrder) : Long

    @Insert
    suspend fun insertOrder(order: Order) : Long

    suspend fun insertInitialData() {
        insertFish(Fish(1, "crvena neonka", 1.0f))
        insertFish(Fish(2, "bakrena tetra", 1.0f))
        insertFish(Fish(3, "ember tetra", 1.5f))
        insertFish(Fish(4, "plamena tetra", 1.0f))

    }
}