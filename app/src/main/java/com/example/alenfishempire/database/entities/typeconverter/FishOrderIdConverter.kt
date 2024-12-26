package com.example.alenfishempire.database.entities.typeconverter

import androidx.room.TypeConverter

class FishOrderIdConverter {

    @TypeConverter
    fun fromFishOrderIdList(fishOrderIdList: List<Long>?): String? {
        return fishOrderIdList?.joinToString(",")
    }

    @TypeConverter
    fun toFishOrderIdList(fishOrderIdString: String?): List<Long>? {
        return fishOrderIdString?.split(",")?.map { it.toLong() }
    }
}