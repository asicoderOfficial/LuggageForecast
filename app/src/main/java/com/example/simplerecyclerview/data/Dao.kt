package com.example.simplerecyclerview.data

import androidx.lifecycle.LiveData
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

    @Query("SELECT COUNT(Name) FROM Trips")
    fun getNumberOfTrips(): Int

    @Query("SELECT * FROM Trips ORDER BY Start DESC")
    fun getAllTrips(): List<DataClass>
}