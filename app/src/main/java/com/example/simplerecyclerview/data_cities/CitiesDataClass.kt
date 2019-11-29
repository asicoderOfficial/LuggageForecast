package com.example.simplerecyclerview.data_cities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CITIES")
data class CitiesDataClass(
    @PrimaryKey
    @ColumnInfo(name = "ID")
    val id: Int,
    @ColumnInfo(name = "CITY")
    val city: String,
    @ColumnInfo(name = "COUNTRY")
    val country: String,
    @ColumnInfo(name = "LON")
    val lon: Float,
    @ColumnInfo(name = "LAT")
    val lat: Float
)