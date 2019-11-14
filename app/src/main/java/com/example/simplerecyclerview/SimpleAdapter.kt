package com.example.simplerecyclerview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecyclerview.data.DataClass
import kotlinx.android.synthetic.main.new_item.view.*
import timber.log.Timber

class SimpleAdapter(val tripsList: ArrayList<DataClass>, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.i("onBindViewHolder called. Position: $position")
        holder.name.text = tripsList.get(position).name
        holder.destiny.text = tripsList.get(position).destination
        holder.start.text = tripsList.get(position).start
        holder.end.text = tripsList.get(position).end
        holder.menuCardView.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.menuCardView)
            popupMenu.inflate(R.menu.trip_cardview_menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.eraseTrip -> Toast.makeText(context, "ERASE", Toast.LENGTH_SHORT).show()
                    R.id.editTrip -> Toast.makeText(context, "EDIT", Toast.LENGTH_SHORT).show()
                }
                true
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Timber.i("onCreateViewHolder called. ViewTipe: $viewType")
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.new_item, parent, false))
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
}