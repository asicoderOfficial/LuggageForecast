package com.example.simplerecyclerview.fragments.main_fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.simplerecyclerview.data_luggages.LuggageDao
import com.example.simplerecyclerview.data_luggages.LuggageDataClass
import com.example.simplerecyclerview.data_trips.TripsDao
import com.example.simplerecyclerview.data_trips.TripsDataClass
import com.example.simplerecyclerview.fragments.luggage_fragment.luggage_recycler.LuggageFragment.Companion.rvAdapter
import com.example.simplerecyclerview.fragments.main_fragment.MainFragment.Companion.citiesList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(val tripsDB: TripsDao, val luggagesDB: LuggageDao, application: Application) :
    AndroidViewModel(application) {

    var tripsList: ArrayList<TripsDataClass> = ArrayList()
    var luggagesList: ArrayList<LuggageDataClass> = ArrayList()

    init {
        if (tripsList.isEmpty() && tripsDB.getNumberOfTrips() != 0)
            tripsList.addAll(tripsDB.getAllTrips())
        if (luggagesDB.getNumberOfLuggages() != 0)
            luggagesList.addAll(luggagesDB.getAllLugages())
    }

    fun addTrip(newTrip: TripsDataClass) {
        tripsList.add(newTrip)
        tripsDB.insert(newTrip)
    }

    fun updateTrip(newTrip: TripsDataClass, position: Int) {
        tripsList[position] = newTrip
        tripsDB.update(newTrip)
    }

    fun eraseTrip(position: Int) {
        tripsDB.delete(tripsList[position])
        tripsList.remove(tripsList[position])
        luggagesDB.delete(luggagesList[position])
        luggagesList.removeAt(position)
        MainFragment.rvAdapter.notifyItemRemoved(position)
        MainFragment.rvAdapter.notifyItemRangeChanged(position, tripsList.size)
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