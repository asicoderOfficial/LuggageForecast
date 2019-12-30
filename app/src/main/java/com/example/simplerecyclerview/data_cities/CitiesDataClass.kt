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
    val city: String? = null
)