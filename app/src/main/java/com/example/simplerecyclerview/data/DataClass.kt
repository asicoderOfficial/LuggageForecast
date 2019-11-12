package com.example.simplerecyclerview.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Trips")
data class DataClass(
    @PrimaryKey
    @ColumnInfo(name = "Name")
    val name: String,
    @ColumnInfo(name = "Destination")
    val destination: String,
    @ColumnInfo(name = "Start")
    val start: String,
    @ColumnInfo(name = "End")
    val end: String
)