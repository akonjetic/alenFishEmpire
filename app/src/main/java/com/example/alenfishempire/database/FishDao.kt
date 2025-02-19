package com.example.alenfishempire.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.FishSalesStats
import com.example.alenfishempire.database.entities.Order
import com.example.alenfishempire.database.entities.OrderWithDetails
import com.example.alenfishempire.database.entities.OrderWithDetailsRaw

@Dao
interface FishDao {
    @Query("SELECT * FROM Fish")
    suspend fun getAllFish(): List<Fish>

    @Query("SELECT * FROM FishOrder Where fishOrderId = :id")
    suspend fun getFishOrderById(id: Long): FishOrder

    @Query("SELECT * FROM Fish Where fishId = :id")
    suspend fun getFishById(id: Long): Fish

    @Query("Select * from 'Order' Order By orderId DESC")
    suspend fun getAllOrdersDesc(): List<Order>


    @Query(
        """
    SELECT 
        o.orderId AS id, 
        o.orderDate AS date, 
        o.fishOrderIsFree AS discount, 
        SUM(
            CASE 
                WHEN fo.fishOrderIsFree = 1 THEN 0
                ELSE (fo.fishOrderQuantity * f.fishPrice)
            END
        ) AS totalPrice,
        SUM(fo.fishOrderQuantity) AS totalQuantity,
        GROUP_CONCAT(f.fishName || ':' || fo.fishOrderQuantity || ':' || f.fishPrice, ';') AS fishDetails
        
    FROM 'Order' o
    JOIN FishOrder fo ON ',' || o.fishOrderIdList || ',' LIKE '%,' || fo.fishOrderId || ',%'
    JOIN Fish f ON fo.fishInOrderId = f.fishId
    
    WHERE (:startDate IS NULL OR :endDate IS NULL OR 
           date(o.orderDate) BETWEEN date(:startDate) AND date(:endDate))  
    
    GROUP BY o.orderId
    
    ORDER BY
        CASE WHEN :sortBy = 'date_desc' THEN o.orderDate END DESC,
        CASE WHEN :sortBy = 'date_asc' THEN o.orderDate END ASC,
        CASE WHEN :sortBy = 'price' THEN totalPrice END DESC,
        CASE WHEN :sortBy = 'quantity' THEN totalQuantity END DESC
    """
    )
    suspend fun getFilteredAndSortedOrders(
        startDate: String?,
        endDate: String?,
        sortBy: String
    ): List<OrderWithDetailsRaw> // Privremena klasa


    /*
    * @Query(
    """
    SELECT
        o.orderId AS id,
        o.orderDate AS date,
        o.fishOrderIsFree AS discount,
        SUM(
            CASE
                WHEN fo.fishOrderIsFree = 1 THEN 0
                ELSE (fo.fishOrderQuantity * f.fishPrice)
            END
        ) AS totalPrice,
        SUM(fo.fishOrderQuantity) AS totalQuantity,
        GROUP_CONCAT(f.fishName || ':' || fo.fishOrderQuantity || ':' || f.fishPrice, ';') AS fishDetails  -- Novi podatak

    FROM 'Order' o
    JOIN FishOrder fo ON ',' || o.fishOrderIdList || ',' LIKE '%,' || fo.fishOrderId || ',%'
    JOIN Fish f ON fo.fishInOrderId = f.fishId

    WHERE (:startDate IS NULL OR :endDate IS NULL OR
           date(o.orderDate) BETWEEN date(:startDate) AND date(:endDate))

    GROUP BY o.orderId

    ORDER BY
        CASE WHEN :sortBy = 'date_desc' THEN o.orderDate END DESC,
        CASE WHEN :sortBy = 'date_asc' THEN o.orderDate END ASC,
        CASE WHEN :sortBy = 'price' THEN totalPrice END DESC,
        CASE WHEN :sortBy = 'quantity' THEN totalQuantity END DESC
    """
)
suspend fun getFilteredAndSortedOrders(
    startDate: String?,
    endDate: String?,
    sortBy: String
): List<OrderWithDetailsRaw> // Privremena klasa
*/

    @Query("SELECT * FROM 'Order' ORDER BY orderDate DESC")
    suspend fun getAllOrders(): List<Order>

    @Query(
        """
    SELECT o.*         
    FROM 'Order' o
    WHERE strftime('%m', o.orderDate / 1000, 'unixepoch') = :month
    AND strftime('%Y', o.orderDate / 1000, 'unixepoch') = :year
"""
    )
    suspend fun getOrdersByMonthAndYear(
        month: String?,
        year: String?
    ): List<Order>


    @Query("SELECT strftime('%m', orderDate / 1000, 'unixepoch') AS month, strftime('%Y', orderDate / 1000, 'unixepoch') AS year FROM 'Order'")
    suspend fun getAllOrderDates(): List<OrderDate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFish(fish: Fish)


    @Insert
    suspend fun insertFishOrder(fishOrder: FishOrder): Long

    @Insert
    suspend fun insertOrder(order: Order): Long


    @Update
    suspend fun updateFish(fish: Fish)


    @Delete
    suspend fun deleteFish(fish: Fish)

    @Query(
        """
    SELECT Fish.fishName AS fishName, 
           SUM(FishOrder.fishOrderQuantity) AS totalQuantity, 
           SUM(CASE WHEN FishOrder.fishOrderIsFree = 0 THEN FishOrder.fishOrderQuantity * Fish.fishPrice ELSE 0 END) AS totalSales, 
           SUM(CASE WHEN FishOrder.fishOrderIsFree = 1 THEN FishOrder.fishOrderQuantity ELSE 0 END) AS totalFree 
    FROM FishOrder 
    JOIN Fish ON FishOrder.fishInOrderId = Fish.fishId
    GROUP BY Fish.fishId
"""
    )
    suspend fun getTotalFishSalesByType(): List<FishSalesStats>


    @Query("SELECT SUM(fishOrderQuantity * fishPrice) FROM FishOrder JOIN Fish ON FishOrder.fishInOrderId = Fish.fishId")
    suspend fun getTotalSales(): Float

    @Query("SELECT SUM(fishOrderQuantity) FROM FishOrder WHERE fishOrderIsFree = 1")
    suspend fun getTotalFreeFish(): Int

    suspend fun insertInitialData() {
        insertFish(Fish(1, "Crvena Neonka", 1.0f))
        insertFish(Fish(2, "Bakrena Tetra", 1.0f))
        insertFish(Fish(3, "Ember Tetra", 1.0f))
        insertFish(Fish(4, "Plamena Tetra", 1.0f))

        insertFish(Fish(5, "Palmeri Tetra", 1.35f))
        insertFish(Fish(6, "Rhodostomus", 1.35f))
        insertFish(Fish(7, "Crvena Fantom Tetra", 1.35f))
        insertFish(Fish(8, "Crna Fantom Tetra", 1.35f))
        insertFish(Fish(9, "Ornatus White Fin", 1.35f))

        insertFish(Fish(10, "Color Tetra", 1.5f))

        insertFish(Fish(11, "Congo Tetra", 2.0f))
        insertFish(Fish(12, "Congo Albino", 2.0f))
        insertFish(Fish(13, "Olovčice", 2.0f))

        insertFish(Fish(14, "Galaxy Razbora", 2.65f))


    }
}

data class OrderDate(
    val month: String,
    val year: String
)

