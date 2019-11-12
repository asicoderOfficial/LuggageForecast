package com.example.simplerecyclerview.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataClass::class], version = 1)
abstract class DatabaseClass : RoomDatabase() {
    abstract fun newDao(): Dao

    companion object {
        var INSTANCE: DatabaseClass? = null

        fun getAppDataBase(context: Context): DatabaseClass? {
            if (INSTANCE == null) {
                synchronized(DatabaseClass::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseClass::class.java,
                        "myDB"
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