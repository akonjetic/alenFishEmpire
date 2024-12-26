package com.example.alenfishempire.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.alenfishempire.database.entities.typeconverter.DateConverter
import com.example.alenfishempire.database.entities.typeconverter.FishOrderIdConverter
import java.io.Serializable
import java.util.Date

@Entity(tableName = "Order")
data class Order (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "orderId")
    val id: Long,
    @ColumnInfo(name = "orderDate")
    @TypeConverters(DateConverter::class)
    val date: Date,
    @ColumnInfo(name = "fishOrderIdList")
    @TypeConverters(FishOrderIdConverter::class)
    val fishOrderId: List<Long>,
    @ColumnInfo(name = "fishOrderIsFree")
    val discount: Float?
) : Serializable