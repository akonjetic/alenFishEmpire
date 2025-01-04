package com.example.alenfishempire.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.Order
import com.example.alenfishempire.database.entities.OrderWithDetails
import java.util.Date

@Dao
interface FishDao {
    //QUERY
    @Query("SELECT * FROM Fish")
    suspend fun getAllFish(): List<Fish>

    @Query("SELECT * FROM FishOrder Where fishOrderId = :id")
    suspend fun getFishOrderById(id : Long): FishOrder

    @Query("SELECT * FROM Fish Where fishId = :id")
    suspend fun getFishById(id : Long): Fish

    @Query("Select * from 'Order' Order By orderId DESC")
    suspend fun getAllOrdersDesc() : List<Order>


    //TODO popravit discount u Orderu
    @Query("""
    SELECT o.orderId as id, o.orderDate as date, o.fishOrderIdList as fishOrderId, o.fishOrderIsFree as discount,         
        SUM(
            CASE 
                WHEN fo.fishOrderIsFree = 1 THEN 0
                ELSE (fo.fishOrderQuantity * f.fishPrice)
            END
        ) AS totalPrice,
        SUM(fo.fishOrderQuantity) AS totalQuantity
    FROM 'Order' o
    JOIN FishOrder fo ON ',' || o.fishOrderIdList || ',' LIKE '%,' || fo.fishOrderId || ',%'
    JOIN Fish f ON fo.fishInOrderId = f.fishId
    WHERE (:month IS NULL OR :month = '' OR strftime('%m', o.orderDate / 1000, 'unixepoch') = :month)
    AND (:year IS NULL OR :year = '' OR strftime('%Y', o.orderDate / 1000, 'unixepoch') = :year)
    GROUP BY o.orderId
    ORDER BY 
        CASE WHEN :sortBy = 'date_desc' THEN o.orderDate END DESC,
        CASE WHEN :sortBy = 'date_asc' THEN o.orderDate END ASC,
        CASE WHEN :sortBy = 'price' THEN totalPrice END DESC,
        CASE WHEN :sortBy = 'quantity' THEN totalQuantity END DESC
""")
    suspend fun getFilteredAndSortedOrders(
        month: String?,
        year: String?,
        sortBy: String
    ): List<OrderWithDetails>

    @Query("SELECT * FROM 'Order' ORDER BY orderDate DESC")
    suspend fun getAllOrders(): List<Order>

    @Query("""
    SELECT o.*         
    FROM 'Order' o
    WHERE strftime('%m', o.orderDate / 1000, 'unixepoch') = :month
    AND strftime('%Y', o.orderDate / 1000, 'unixepoch') = :year
""")
    suspend fun getOrdersByMonthAndYear(
        month: String?,
        year: String?
    ): List<Order>


    @Query("SELECT strftime('%m', orderDate / 1000, 'unixepoch') AS month, strftime('%Y', orderDate / 1000, 'unixepoch') AS year FROM 'Order'")
    suspend fun getAllOrderDates(): List<OrderDate>

    //INSERT
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFish(fish: Fish)

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

data class OrderDate(
    val month: String,
    val year: String
)
