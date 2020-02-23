package com.example.simplerecyclerview.fragments.weather_recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecyclerview.R
import kotlinx.android.synthetic.main.weather_item.view.*

class WeatherAdapter(
    private var params: ArrayList<Array<Double>>,
    private var dates: ArrayList<String>,
    private var context: Context?
) :
    RecyclerView.Adapter<WeatherViewHolder>() {

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.dateAndTime.text = dates[position]
        holder.temp.text = "Temperature: ${params[position][0]}"
        holder.tempMin.text = "Min. Temperature: ${params[position][1]}"
        holder.tempMax.text = "Max. Temperature: ${params[position][2]}"
        when (params[position][3]) {
            in 200.0..232.0 -> holder.weatherImage.setImageResource(R.drawable.storm)
            in 300.0..321.0 -> holder.weatherImage.setImageResource(R.drawable.rain_and_sun)
            in 500.0..504.0 -> holder.weatherImage.setImageResource(R.drawable.rain)
            511.0 -> holder.weatherImage.setImageResource(R.drawable.snowing)
            in 520.0..531.0 -> holder.weatherImage.setImageResource(R.drawable.rain)
            in 600.0..622.0 -> holder.weatherImage.setImageResource(R.drawable.snowing)
            in 701.0..781.0 -> holder.weatherImage.setImageResource(R.drawable.foggy)
            800.0 -> holder.weatherImage.setImageResource(R.drawable.sun)
            801.0 -> holder.weatherImage.setImageResource(R.drawable.cloudy)
            802.0 -> holder.weatherImage.setImageResource(R.drawable.cloudy)
            803.0 -> holder.weatherImage.setImageResource(R.drawable.clouds)
            804.0 -> holder.weatherImage.setImageResource(R.drawable.clouds)
        }
        holder.windSpeed.text = "Wind speed: " + params[position][6]
        holder.windTemp.text = "Wind temp: ${params[position][7]}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.weather_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return params.size
    }


}

class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val dateAndTime: TextView = view.dateTimeTV
    val temp: TextView = view.tempTV
    val tempMin: TextView = view.tempMinTV
    val tempMax: TextView = view.tempMaxTV
    val windSpeed: TextView = view.windSpeedTV
    val windTemp: TextView = view.windTempTV

    val weatherImage: ImageView = view.weatherIV
}