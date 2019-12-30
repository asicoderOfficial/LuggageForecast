package com.example.simplerecyclerview.data_cities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//the name is cities in the last version
@Entity(tableName = "CITIES")
data class CitiesDataClass(
    @PrimaryKey
    @ColumnInfo(name = "ID")
    val id: Int? = null,
    @ColumnInfo(name = "CITY")
    val city: String? = null,
    @ColumnInfo(name = "COUNTRY")
    val country: String? = null,
    @ColumnInfo(name = "LON")
    val lon: Float? = null,
    @ColumnInfo(name = "LAT")
    val lat: Float? = null
)