package com.example.alenfishempire.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.Order

@Database(entities = [Fish::class, Order::class, FishOrder::class], version = 1, exportSchema = false)
abstract class FishDatabase : RoomDatabase() {

    abstract fun getFishDao(): FishDao

    companion object {
        private var instance: FishDatabase? = null

        fun getDatabase(context: Context): FishDatabase? {
            if (instance == null) {
                instance = buildDatabase(context)
            }
            return instance
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                FishDatabase::class.java,
                "FishDatabase"
            )
                .fallbackToDestructiveMigration()
                .build()

    }
}