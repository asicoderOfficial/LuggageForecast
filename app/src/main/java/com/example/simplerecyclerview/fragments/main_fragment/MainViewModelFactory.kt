package com.example.simplerecyclerview.fragments.main_fragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplerecyclerview.data_luggages.LuggageDao
import com.example.simplerecyclerview.data_trips.TripsDao

class MainViewModelFactory(
    private val tripsDB: TripsDao,
    private val luggagesDB: LuggageDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(tripsDB, luggagesDB, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}