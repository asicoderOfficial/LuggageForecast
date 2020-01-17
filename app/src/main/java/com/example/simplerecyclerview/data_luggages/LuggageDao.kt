package com.example.simplerecyclerview.data_luggages

import androidx.room.*
import com.example.simplerecyclerview.data_trips.TripsDataClass

interface LuggageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newLuggagesData: LuggageDataClass)

    @Delete
    fun delete(newLuggagesData: LuggageDataClass)

    @Update
    fun update(newLuggagesData: LuggageDataClass)

    @Query("SELECT COUNT(Name) FROM Trips")
    fun getNumberOfTrips(): Int

    @Query("SELECT * FROM Trips ORDER BY Start DESC")
    fun getAllTrips(): List<TripsDataClass>
}