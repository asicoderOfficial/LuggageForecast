package com.example.simplerecyclerview.data_luggages

import androidx.room.*
import com.example.simplerecyclerview.data_trips.TripsDataClass

@Dao
interface LuggageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newLuggagesData: LuggageDataClass)

    @Delete
    fun delete(newLuggagesData: LuggageDataClass)

    @Update
    fun update(newLuggagesData: LuggageDataClass)
}