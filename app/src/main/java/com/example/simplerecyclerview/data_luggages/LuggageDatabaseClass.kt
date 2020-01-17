package com.example.simplerecyclerview.data_luggages

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.simplerecyclerview.data_trips.TripsDao
import com.example.simplerecyclerview.data_trips.TripsDataClass

@Database(entities = [LuggageDataClass::class], version = 1)
abstract class LuggageDatabaseClass : RoomDatabase() {
    abstract fun luggagesDao(): TripsDao

    companion object {
        var INSTANCE: LuggageDatabaseClass? = null

        fun getAppDataBase(context: Context): LuggageDatabaseClass? {
            if (INSTANCE == null) {
                synchronized(LuggageDatabaseClass::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LuggageDatabaseClass::class.java,
                        "LuggagesDB"
                    )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}