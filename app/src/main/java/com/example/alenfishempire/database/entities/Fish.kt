package com.example.alenfishempire.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Fish")
data class Fish (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "fishId")
    val id: Long,
    @ColumnInfo(name = "fishName")
    val name: String,
    @ColumnInfo(name = "fishPrice")
    val price: Float
) : Serializable

data class FishDTO(
    val name: String,
    val price: Float
)