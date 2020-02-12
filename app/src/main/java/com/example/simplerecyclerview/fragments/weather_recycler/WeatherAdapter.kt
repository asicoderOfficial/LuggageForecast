package com.example.simplerecyclerview.fragments.weather_recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecyclerview.R
import kotlinx.android.synthetic.main.weather_item.view.*

class WeatherAdapter(
    private var params: ArrayList<Array<Double>>,
    private var dates: ArrayList<String>,
    private var context: Context
) :
    RecyclerView.Adapter<WeatherViewHolder>() {

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.dateAndTime.text = dates[position]
        holder.temp.text = "Temperature: ${params[position][0]}"
        holder.tempMin.text = "Min. Temperature: ${params[position][1]}"
        holder.tempMax.text = "Max. Temperature: ${params[position][2]}"
        when (params[position][3]) {
            in 200.0..232.0 -> holder.weatherImage.setImageResource(R.drawable.thunderstorm)
            in 300.0..321.0 -> holder.weatherImage.setImageResource(R.drawable.shower_rain)
            in 500.0..504.0 -> holder.weatherImage.setImageResource(R.drawable.rain)
            511.0 -> holder.weatherImage.setImageResource(R.drawable.snow)
            in 520.0..531.0 -> holder.weatherImage.setImageResource(R.drawable.shower_rain)
            in 600.0..622.0 -> holder.weatherImage.setImageResource(R.drawable.snow)
            in 701.0..781.0 -> holder.weatherImage.setImageResource(R.drawable.mist)
            800.0 -> holder.weatherImage.setImageResource(R.drawable.clear_sky)
            801.0 -> holder.weatherImage.setImageResource(R.drawable.few_clouds)
            802.0 -> holder.weatherImage.setImageResource(R.drawable.scattered_clouds)
            803.0 -> holder.weatherImage.setImageResource(R.drawable.broken_clouds)
            804.0 -> holder.weatherImage.setImageResource(R.drawable.broken_clouds)
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
    val dateAndTime = view.dateTimeTV
    val temp = view.tempTV
    val tempMin = view.tempMinTV
    val tempMax = view.tempMaxTV
    val windSpeed = view.windSpeedTV
    val windTemp = view.windTempTV

    val weatherImage = view.weatherIV
}