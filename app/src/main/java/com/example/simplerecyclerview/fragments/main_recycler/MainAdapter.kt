package com.example.simplerecyclerview.fragments.main_recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecyclerview.JsonParserService
import com.example.simplerecyclerview.R
import com.example.simplerecyclerview.data_trips.TripsDataClass
import com.example.simplerecyclerview.fragments.LuggageFragment
import com.example.simplerecyclerview.fragments.WeatherFragment
import kotlinx.android.synthetic.main.new_item.view.*

class MainAdapter(
    private var tripsList: ArrayList<TripsDataClass>,
    private val context: Context?,
    private val rvMethods: RV_Methods
) :
    RecyclerView.Adapter<MainViewHolder>() {

    companion object {
        var idCity: String? = null
    }
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.name.text = tripsList[position].name
        holder.destiny.text = tripsList[position].destinationName
        holder.start.text = tripsList[position].start
        holder.end.text = tripsList[position].end

        holder.weatherIBT.setOnClickListener {
            JsonParserService.isForWeatherFragment = true
            idCity = tripsList[position].destinationName
            val intentJson = Intent(context, JsonParserService::class.java)
            context!!.startService(intentJson)
            WeatherFragment.titleFragment = holder.name.text as String
            Navigation.findNavController(it).navigate(R.id.action_mainFragment_to_weatherFragment)
        }

        holder.eraseTripIBT.setOnClickListener {
            rvMethods.onItemEraseClick(position)
        }

        holder.editTripIBT.setOnClickListener {
            rvMethods.onItemEditClick(position, it)
        }

        holder.cardView.setOnClickListener {
            LuggageFragment.titleActionBar = holder.name.text as String
            Navigation.findNavController(it).navigate(R.id.action_mainFragment_to_luggageFragment)
            LuggageFragment.cardViewPressedPos = position
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.new_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return tripsList.size
    }
}


class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name = view.nameoftripTV
    val destiny = view.destinyOfTripTV
    val start = view.startOfTripTV
    val end = view.endOfTripTV

    val editTripIBT = view.editTripIBT
    val eraseTripIBT = view.eraseTripBT
    val weatherIBT = view.weatherIBT

    val cardView = view.rootView

}