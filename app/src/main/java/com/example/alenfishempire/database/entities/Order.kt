package com.example.alenfishempire.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "Order")
data class Order (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "orderId")
    val id: Long,
    @ColumnInfo(name = "orderDate")
    val date: Date,
    @ColumnInfo(name = "fishOrderIdList")
    val fishOrderId: ArrayList<Long>,
    @ColumnInfo(name = "fishOrderIsFree")
    val discount: Float?
) : Serializable