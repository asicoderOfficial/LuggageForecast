package com.example.simplerecyclerview.fragments.luggage.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecyclerview.R
import kotlinx.android.synthetic.main.luggage_item.view.*
import kotlinx.android.synthetic.main.luggage_item.view.numberOfClothesTV

/**
 * Adapter of the RecyclerView of LuggageFragment that displays your luggage in CardViews.
 *
 * @author Asicoder
 */
class LuggageAdapter(
    private var luggageList: ArrayList<Pair<String, Int>>,
    private val context: Context?
) : RecyclerView.Adapter<LuggagesViewHolder>() {

    override fun onBindViewHolder(holder: LuggagesViewHolder, position: Int) {
        holder.nameOfClothes.text = luggageList[position].first
        holder.numberOfClothes.text = luggageList[position].second.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LuggagesViewHolder {
        return LuggagesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.luggage_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return luggageList.size
    }
}

class LuggagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val nameOfClothes: TextView = view.nameClothesTV
    val numberOfClothes: TextView = view.numberOfClothesTV
}