package com.example.simplerecyclerview

import android.app.IntentService
import android.content.Intent
import com.example.simplerecyclerview.fragments.MainFragment
import com.example.simplerecyclerview.fragments.main_recycler.MainAdapter
import org.json.JSONObject
import timber.log.Timber
import java.net.URL

val API_KEY: String = "dd1670ca8d4a22e19aa20b83753f5dad"

class JsonParserService : IntentService("LuggageCalculationIS") {

    companion object {
        val params = ArrayList<Array<Double>>()
        val dates = ArrayList<String>()
    }

    override fun onHandleIntent(intent: Intent?) {
        jsonParser()
    }

    private fun jsonParser() {
        val response: String?
        try {
            response =
                URL("https://api.openweathermap.org/data/2.5/forecast?id=${MainFragment.sCityID}&units=metric&appid=${API_KEY}").readText(
                    Charsets.UTF_8
                )
            val jsonObj = JSONObject(response)
            val jsonObjList = jsonObj.getJSONArray("list")
            for (i in 0 until jsonObjList.length()) {
                val tempArray = arrayOf(
                    jsonObjList.getJSONObject(i).getJSONObject("main").getDouble("temp"),
                    jsonObjList.getJSONObject(i).getJSONObject("main").getDouble("temp_min"),
                    jsonObjList.getJSONObject(i).getJSONObject("main").getDouble("temp_max"),
                    jsonObjList.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getDouble(
                        "id"
                    ),
                    jsonObjList.getJSONObject(i).getJSONObject("main").getDouble("pressure"),
                    jsonObjList.getJSONObject(i).getJSONObject("main").getDouble("humidity"),
                    jsonObjList.getJSONObject(i).getJSONObject("wind").getDouble("speed"),
                    jsonObjList.getJSONObject(i).getJSONObject("wind").getDouble("deg")
                )
                dates.add(jsonObjList.getJSONObject(i).getString("dt_txt"))
                params.add(tempArray)
            }
        } catch (e: Exception) {
            Timber.i("Exception thrown during json parsing.")
        }
        KnapsackLF.solver(params)
    }
}