package com.example.simplerecyclerview.data_trips

import androidx.room.*

@Dao
interface TripsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newTripsData: TripsDataClass)

    @Delete
    fun delete(newTripsData: TripsDataClass)

    @Update
    fun update(newTripsData: TripsDataClass)

    @Query("SELECT COUNT(Name) FROM Trips")
    fun getNumberOfTrips(): Int

    @Query("SELECT * FROM Trips ORDER BY Start DESC")
    fun getAllTrips(): List<TripsDataClass>

    @Query("DELETE FROM Trips")
    fun deleteAllTrips()

}