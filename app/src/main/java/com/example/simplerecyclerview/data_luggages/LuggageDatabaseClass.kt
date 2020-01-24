package com.example.simplerecyclerview.data_luggages

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LuggageDataClass::class], version = 2, exportSchema = true)
abstract class LuggageDatabaseClass : RoomDatabase() {
    abstract fun luggagesDao(): LuggageDao

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