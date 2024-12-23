package com.example.alenfishempire.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "FishOrder")
data class FishOrder (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "fishOrderId")
    val id: Long,
    @ColumnInfo(name = "fishInOrderId")
    val fishId: Long,
    @ColumnInfo(name = "fishOrderQuantity")
    val quantity: Int,
    @ColumnInfo(name = "fishOrderIsFree")
    val isFree: Boolean
) : Serializable