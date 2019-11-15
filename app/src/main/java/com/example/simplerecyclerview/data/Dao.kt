package com.example.simplerecyclerview.data

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newData: DataClass)

    @Delete
    fun delete(newData: DataClass)

    @Update
    fun update(newData: DataClass)

}