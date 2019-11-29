package com.example.simplerecyclerview.data_cities

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CitiesDao {

    @Query("SELECT COUNT(CITY) FROM CITIES")
    fun getNumberOfCities(): Int

    @Query("SELECT CITY FROM CITIES ORDER BY CITY DESC")
    fun getAllSortedByCity(): List<String>
}