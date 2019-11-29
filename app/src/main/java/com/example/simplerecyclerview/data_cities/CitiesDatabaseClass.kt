package com.example.simplerecyclerview.data_cities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CitiesDataClass::class], version = 1)
abstract class CitiesDatabaseClass : RoomDatabase() {
    abstract fun citiesDao(): CitiesDao

    companion object {
        var INSTANCE: CitiesDatabaseClass? = null

        fun getAppDataBase(context: Context): CitiesDatabaseClass? {
            if (INSTANCE == null) {
                synchronized(CitiesDatabaseClass::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CitiesDatabaseClass::class.java,
                        "CITIES"
                    ).allowMainThreadQueries()
                        .createFromAsset("databases/stations_db.sqlite")
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