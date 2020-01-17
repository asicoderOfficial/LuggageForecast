package com.example.simplerecyclerview

import android.app.Service
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.net.URL

val API_KEY: String = "4377883cd1f33629c0abd644adf0f399"

class JsonParserService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    class WeatherGetter : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response =
                    URL("api.openweathermap.org/data/2.5/forecast?id={${MainActivity.sCityID.toString()}}&cnt={${MainActivity.tripDurationDays}&appid=${API_KEY}").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result!!)
                val jsonObjList = JSONArray(jsonObj.getJSONArray("list"))
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val humidity = main.getString("humidity")
            } catch (e: Exception) {
                Timber.i("Exception thrown")
            }
        }
    }
}
