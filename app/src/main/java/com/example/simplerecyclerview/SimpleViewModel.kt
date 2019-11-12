package com.example.simplerecyclerview

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.simplerecyclerview.data.Dao
import com.example.simplerecyclerview.data.DataClass

class SimpleViewModel : ViewModel() {
    private var dao: Dao? = null

    fun insertNewTrip(newData: DataClass) {
        dao?.insert(newData)
    }

    fun eraseTrip(newData: DataClass) {

    }
}