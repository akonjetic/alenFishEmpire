package com.example.alenfishempire.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "FishOrder")
data class FishOrder (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "fishOrderId")
    var id: Long,
    @ColumnInfo(name = "fishInOrderId")
    val fishId: Long,
    @ColumnInfo(name = "fishOrderQuantity")
    val quantity: Int,
    @ColumnInfo(name = "fishOrderIsFree")
    val isFree: Boolean
) : Serializable

data class FishSalesStats(
    val fishName: String,
    val totalQuantity: Int,
    val totalSales: Float,
    val totalFree: Int
)

data class FishOrderItem(
    var fishType: String = "",
    var quantity: Int = 0,
    var price: Float = 0.0f,
    var isFree: Boolean = false
)
