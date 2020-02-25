package com.example.simplerecyclerview.fragments.main_fragment

import androidx.lifecycle.ViewModel
import com.example.simplerecyclerview.fragments.main_fragment.MainFragment.Companion.citiesList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel : ViewModel() {

    init {

    }

    fun getDestinationTime(dateDestination: String): Long {

        val unixTimeDestination = Calendar.getInstance()
        unixTimeDestination.set(
            dateDestination.substring(6, 10).toInt(),
            dateDestination.substring(3, 5).toInt(),
            dateDestination.substring(0, 2).toInt(),
            0, 0, 0
        )
        val inMillies = unixTimeDestination.timeInMillis
        return inMillies
    }
}