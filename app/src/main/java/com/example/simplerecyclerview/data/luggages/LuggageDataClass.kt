package com.example.simplerecyclerview.data.luggages

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Luggages")
class LuggageDataClass(
    @PrimaryKey
    @ColumnInfo(name = "Index")
    val cardViewPos: Int,

    //////// UPPPER BODY ////////////////
    @ColumnInfo(name = "T-Shirts")
    val t_shirts: Int?,
    @ColumnInfo(name = "Jacket")
    val jacket: Int?,
    @ColumnInfo(name = "Coat")
    val coat: Int?,
    @ColumnInfo(name = "Long-sleeved T-shirts")
    val long_sleevedT_shirts: Int?,

    //////// LOWER BODY ////////////////
    @ColumnInfo(name = "Shorts")
    val shorts: Int?,
    @ColumnInfo(name = "Trousers")
    val trousers: Int?,
    @ColumnInfo(name = "Shoes")
    val shoes: Int?,

    ///////// UNDERCLOTHES /////////////
    @ColumnInfo(name = "Underpants")
    val underpants: Int?,
    @ColumnInfo(name = "Socks")
    val socks: Int?,

    ///////// RAINY ///////////////////
    @ColumnInfo(name = "Umbrella")
    val umbrella: Int?,
    @ColumnInfo(name = "Raincoat")
    val raincoat: Int?,

    ///////// COLD ////////////////////
    @ColumnInfo(name = "Hat")
    val hat: Int?,
    @ColumnInfo(name = "Gloves")
    val gloves: Int?,
    @ColumnInfo(name = "Scarf")
    val scarf: Int?
)