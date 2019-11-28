package com.example.simplerecyclerview.data_trips

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TripsDataClass::class], version = 1)
abstract class TripsDatabaseClass : RoomDatabase() {
    abstract fun newDao(): TripsDao

    companion object {
        var INSTANCE: TripsDatabaseClass? = null

        fun getAppDataBase(context: Context): TripsDatabaseClass? {
            if (INSTANCE == null) {
                synchronized(TripsDatabaseClass::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        TripsDatabaseClass::class.java,
                        "TripsDB"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}