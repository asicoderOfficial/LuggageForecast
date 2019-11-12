package com.example.simplerecyclerview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecyclerview.data.DataClass
import kotlinx.android.synthetic.main.new_item.view.*

class SimpleAdapter(val tripsList: ArrayList<Trip>, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("onBindViewHolder", "onBindViewHolder called. Position: $position")
        holder.name.text = tripsList.get(position).name
        holder.destiny.text = tripsList.get(position).destiny
        holder.start.text = tripsList.get(position).start
        holder.end.text = tripsList.get(position).end
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i("onCreateViewHolder", "onCreateViewHolder called. ViewTipe: $viewType")
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
}