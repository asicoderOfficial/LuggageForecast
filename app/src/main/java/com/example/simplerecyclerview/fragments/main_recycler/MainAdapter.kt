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
import kotlinx.android.synthetic.main.new_item.view.*
import timber.log.Timber

class MainAdapter(
    private var tripsList: ArrayList<TripsDataClass>,
    private val context: Context?,
    private val rvMethods: RV_Methods
) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = tripsList[position].name
        holder.destiny.text = tripsList[position].destinationName
        holder.start.text = tripsList[position].start
        holder.end.text = tripsList[position].end
        /*holder.itemView.setOnClickListener {
            holder.itemView.findNavController().navigate(R.id.luggageFragment)
        }*/
        holder.menuCardView.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.menuCardView)
            popupMenu.inflate(R.menu.trip_cardview_menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.eraseTrip -> rvMethods.onItemEraseClick(position)

                    R.id.editTrip -> rvMethods.onItemEditClick(position, holder.itemView)
                }
                true
            }
            popupMenu.show()
        }

        holder.cardView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.luggageFragment)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Timber.i("onCreateViewHolder called. ViewTipe: $viewType")
        return ViewHolder(
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


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name = view.nameTextView
    val destiny = view.destinyTextView
    val start = view.startTextView
    val end = view.endTextView
    val menuCardView = view.three_dots_menuIBt
    val cardView = view.rootView
}