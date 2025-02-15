package com.example.alenfishempire.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.Order
import com.example.alenfishempire.database.entities.typeconverter.DateConverter
import com.example.alenfishempire.database.entities.typeconverter.FishOrderIdConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Fish::class, Order::class, FishOrder::class], version = 3, exportSchema = false)
@TypeConverters(DateConverter::class, FishOrderIdConverter::class)
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

        private fun buildDatabase(context: Context): FishDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FishDatabase::class.java,
                "FishDatabase"
            )
                .fallbackToDestructiveMigration()
                .addCallback(roomDatabaseCallback)
                .build()
        }

        private val roomDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Pokreni seeding podataka u pozadini
                CoroutineScope(Dispatchers.IO).launch {
                    instance?.getFishDao()?.insertInitialData()
                }
            }
        }

    }
}