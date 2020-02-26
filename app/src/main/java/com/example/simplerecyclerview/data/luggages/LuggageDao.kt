package com.example.simplerecyclerview.data.luggages

import androidx.room.*

@Dao
interface LuggageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newLuggagesData: LuggageDataClass)

    @Delete
    fun delete(newLuggagesData: LuggageDataClass)

    @Update
    fun update(newLuggagesData: LuggageDataClass)

    @Query("SELECT COUNT(`Index`) FROM Luggages")
    fun getNumberOfLuggages(): Int

    @Query("SELECT * FROM Luggages")
    fun getAllLugages(): List<LuggageDataClass>

    @Query("DELETE FROM Luggages")
    fun deleteAllLuggages()

}