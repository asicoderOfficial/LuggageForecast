package com.example.simplerecyclerview

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.simplerecyclerview.fragments.MainFragment
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.*
import java.lang.Exception
import java.net.URL
import java.util.function.DoubleConsumer
import kotlin.coroutines.coroutineContext

val API_KEY: String = "dd1670ca8d4a22e19aa20b83753f5dad"

class JsonParserService : IntentService("LuggageCalculationIS") {

    override fun onHandleIntent(intent: Intent?) {
        jsonParser()
    }

    private fun jsonParser() {
        val response: String?
        val params = arrayListOf<Array<Double>>()
        try {
            response =
                URL("https://api.openweathermap.org/data/2.5/forecast?id=${MainFragment.sCityID}&units=metric&appid=${API_KEY}").readText(
                    Charsets.UTF_8
                )
            val jsonObj = JSONObject(response)
            val jsonObjList = jsonObj.getJSONArray("list")
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
        } catch (e: Exception) {
            Timber.i("Exception thrown during json parsing.")
        }
        KnapsackLF.solver(params)
    }
}
