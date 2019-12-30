package com.example.simplerecyclerview.data_cities

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CitiesDao {

    @Query("SELECT COUNT(CITY) FROM cities")
    fun getNumberOfCities(): Int

    @Query("SELECT CITY FROM cities ORDER BY CITY DESC")
    fun getAllSortedByCity(): List<String>
}