package com.example.simplerecyclerview.data_trips

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Trips")
data class TripsDataClass(
    @PrimaryKey
    @ColumnInfo(name = "Name")
    val name: String,
    @ColumnInfo(name = "Destination Name")
    val destinationName: String,
    @ColumnInfo(name = "Destination ID")
    val destinationID: String,
    @ColumnInfo(name = "Start")
    val start: String,
    @ColumnInfo(name = "End")
    val end: String
)