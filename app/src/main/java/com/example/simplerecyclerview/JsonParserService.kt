package com.example.simplerecyclerview

import android.app.Service
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.*
import java.lang.Exception
import java.net.URL
import java.util.function.DoubleConsumer
import kotlin.coroutines.coroutineContext

val API_KEY: String = "dd1670ca8d4a22e19aa20b83753f5dad"

class JsonParserService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    class WeatherGetter : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            var response: String? = null
            try {
                //response = URL("api.openweathermap.org/data/2.5/forecast/daily?id=524901").readText(Charsets.UTF_8)
                // https://api.openweathermap.org/data/2.5/forecast?id={${707860}}&units=metric&appid=${dd1670ca8d4a22e19aa20b83753f5dad}
                // https://api.openweathermap.org/data/2.5/forecast?id=3130360&units=metric&appid=dd1670ca8d4a22e19aa20b83753f5dad
                response =
                    URL("https://api.openweathermap.org/data/2.5/forecast?id=${MainActivity.sCityID}&units=metric&appid=${API_KEY}").readText(
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
                val jsonObjList = jsonObj.getJSONArray("list")
                val params = arrayListOf<Array<Double>>()
                for (i in 0 until jsonObjList.length()) {
                    val tempArray = arrayOf(
                        jsonObjList.getJSONObject(i).getJSONObject("main").getDouble("feels_like"),
                        jsonObjList.getJSONObject(i).getJSONObject("main").getDouble("temp_min"),
                        jsonObjList.getJSONObject(i).getJSONObject("main").getDouble("temp_max"),
                        jsonObjList.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getDouble(
                            "id"
                        )
                    )
                    params.add(tempArray)
                }
                println("PARAMS=" + params[0][0])
            } catch (e: Exception) {
                Timber.i("Exception thrown")
            }
        }
    }
}
