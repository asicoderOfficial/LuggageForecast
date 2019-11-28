package com.example.simplerecyclerview.data_cities

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CitiesDao {

    @Query("SELECT CITY FROM Cities ORDER BY CITY DESC")
    fun getAllSortedByCity(): List<String>
}