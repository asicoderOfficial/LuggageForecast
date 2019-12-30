package com.example.simplerecyclerview.data_cities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

@Database(entities = [CitiesDataClass::class], version = 2, exportSchema = true)
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
                        .createFromFile(File("C:\\Users\\asico\\AndroidStudioProjects\\SimpleRecyclerView\\app\\src\\main\\assets\\databases\\cities_SQL.sql"))
                        .fallbackToDestructiveMigration()
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