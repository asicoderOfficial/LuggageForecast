package com.example.simplerecyclerview.fragments.main_recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecyclerview.R
import com.example.simplerecyclerview.data_trips.TripsDataClass
import com.example.simplerecyclerview.fragments.LuggageFragment
import com.example.simplerecyclerview.fragments.MainFragment
import com.example.simplerecyclerview.fragments.WeatherFragment
import com.example.simplerecyclerview.fragments.luggage_recycler.LuggageAdapter
import kotlinx.android.synthetic.main.fragment_luggage.view.*
import kotlinx.android.synthetic.main.new_item.view.*
import timber.log.Timber

class MainAdapter(
    private var tripsList: ArrayList<TripsDataClass>,
    private val context: Context?,
    private val rvMethods: RV_Methods
) :
    RecyclerView.Adapter<MainViewHolder>() {

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.name.append(" " + tripsList[position].name)
        holder.destiny.append(" " + tripsList[position].destinationName)
        holder.start.append(" " + tripsList[position].start)
        holder.end.append(" " + tripsList[position].end)
        /*holder.itemView.setOnClickListener {
            holder.itemView.findNavController().navigate(R.id.luggageFragment)
        }*/

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
    val name = view.nameTextView
    val destiny = view.destinyTextView
    val start = view.startTextView
    val end = view.endTextView
    val editTripIBT = view.editTripIBT
    val eraseTripIBT = view.eraseTripBT
    val cardView = view.rootView
}