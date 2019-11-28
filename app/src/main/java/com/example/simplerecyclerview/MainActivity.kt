package com.example.simplerecyclerview

import android.content.DialogInterface
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.simplerecyclerview.data_cities.CitiesDataClass
import com.example.simplerecyclerview.data_cities.CitiesDatabaseClass
import com.example.simplerecyclerview.data_trips.TripsDataClass
import com.example.simplerecyclerview.data_trips.TripsDatabaseClass
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_data.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var tripsDB: TripsDatabaseClass? = null
    var citiesDB: CitiesDatabaseClass? = null
    private var tripsList: ArrayList<TripsDataClass> = ArrayList()
    private var citiesList: ArrayList<String> = ArrayList()
    private lateinit var rvAdapter: SimpleAdapter
    private val DATE_PATTERN: Pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant()
        Stetho.initializeWithDefaults(this)
        simpleRV.layoutManager = LinearLayoutManager(this)

        tripsDB =
            Room.databaseBuilder(applicationContext, TripsDatabaseClass::class.java, "Trips DB")
                .allowMainThreadQueries().build()
        citiesDB =
            CitiesDatabaseClass.getAppDataBase(this)

        rvAdapter = SimpleAdapter(tripsList, this, object : RV_Methods {
            override fun onItemEditClick(position: Int) {
                editTripPopUp(position)
            }

            override fun onItemEraseClick(position: Int) {
                eraseTrip(position)
            }
        })
        tripsList.addAll(tripsDB!!.newDao().getAllTrips() as ArrayList<TripsDataClass>)
        citiesList.addAll(citiesDB!!.citiesDao().getAllSortedByCity() as ArrayList<String>)

        simpleRV.adapter = rvAdapter

        //parser("http://bulk.openweathermap.org/sample/city.list.json.gz")

        val autoCompleteAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, citiesList)
        addBT.setOnClickListener {
            addTripPopUp(autoCompleteAdapter)
        }
    }

    /*fun parser(url: String) {
        val request = Request.Builder().url(url).build()

        weatherClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(
                    applicationContext,
                    "Failed while trying to access json file.",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val str_response = response.body()!!.string()
                val jsonCity = JSONObject(str_response)
                val jsonArrayCities: JSONArray = jsonCity.getJSONArray("")
                val size: Int = jsonArrayCities.length()
                lateinit var jsonCityDetail: JSONObject
                lateinit var city: City
                for (i in 0 until size) {
                    jsonCityDetail = jsonArrayCities.getJSONObject(i)
                    city = City(
                        jsonCityDetail.getInt("id"),
                        jsonCityDetail.getString("name"),
                        jsonCityDetail.getString("country"),
                        jsonCityDetail.getInt("lon"),
                        jsonCityDetail.getInt("lat")
                    )
                    citiesJSON_list.add(city)
                }
            }
        })
    }*/

    fun addTripPopUp(autoCompleteAdapter: ArrayAdapter<String>) {
        val popUpInflater = layoutInflater.inflate(R.layout.popup_data, null, false)
        popUpInflater.startDateEditText.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.US).format(
                System.currentTimeMillis()
            )
        )
        popUpInflater.endDateEditText.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.US).format(
                System.currentTimeMillis()
            )
        )
        popUpInflater.destinyAutoCTV.threshold = 1
        popUpInflater.destinyAutoCTV.setAdapter(autoCompleteAdapter)

        val popUpBuilder = AlertDialog.Builder(this)
        popUpBuilder.setView(popUpInflater)
        popUpBuilder.setCancelable(false)
        popUpBuilder.setPositiveButton("CREATE") { dialogInterface: DialogInterface, i: Int -> }
        val dialog: AlertDialog =
            popUpBuilder.setNegativeButton("CANCEL") { dialogInterface: DialogInterface, i: Int -> }
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (!textChecker(
                    popUpInflater.nameOfTripEditText.text.toString(),
                    popUpInflater.destinyAutoCTV.text.toString(),
                    popUpInflater.startDateEditText.text.toString(),
                    popUpInflater.endDateEditText.text.toString()
                )
            )
                dialog.dismiss()
            else {
                Thread {
                    val newTrip = TripsDataClass(
                        popUpInflater.nameOfTripEditText.text.toString(),
                        popUpInflater.destinyAutoCTV.text.toString(),
                        popUpInflater.startDateEditText.text.toString(),
                        popUpInflater.endDateEditText.text.toString()
                    )
                    tripsList.add(newTrip)

                    tripsDB?.newDao()?.insert(newTrip)
                }.start()
                rvAdapter.notifyItemInserted(tripsList.size)
                dialog.dismiss()
            }
        }
    }

    fun editTripPopUp(position: Int) {
        val popUpInflater = layoutInflater.inflate(R.layout.popup_data, null, false)
        popUpInflater.nameOfTripEditText.setText(tripsList.get(position).name)
        popUpInflater.destinyAutoCTV.setText(tripsList.get(position).destination)
        popUpInflater.startDateEditText.setText((tripsList.get(position).start))
        popUpInflater.endDateEditText.setText((tripsList.get(position).end))
        val popUpBuilder = AlertDialog.Builder(this)
        popUpBuilder.setView(popUpInflater)
        popUpBuilder.setCancelable(false)
        popUpBuilder.setPositiveButton("CREATE") { dialogInterface: DialogInterface, i: Int -> }
        val dialog: AlertDialog =
            popUpBuilder.setNegativeButton("CANCEL") { dialogInterface: DialogInterface, i: Int -> }
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (!textChecker(
                    popUpInflater.nameOfTripEditText.text.toString(),
                    popUpInflater.destinyAutoCTV.text.toString(),
                    popUpInflater.startDateEditText.text.toString(),
                    popUpInflater.endDateEditText.text.toString()
                )
            )
                dialog.dismiss()
            else {
                Thread {
                    val newTrip = TripsDataClass(
                        popUpInflater.nameOfTripEditText.text.toString(),
                        popUpInflater.destinyAutoCTV.text.toString(),
                        popUpInflater.startDateEditText.text.toString(),
                        popUpInflater.endDateEditText.text.toString()
                    )
                    tripsList.set(position, newTrip)
                    tripsDB?.newDao()?.update(newTrip)
                }.start()
                rvAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }
    }

    fun textChecker(name: String, destiny: String, starting: String, ending: String): Boolean {
        if (name == "")
            Toast.makeText(this, "Name of trip can not be empty.", Toast.LENGTH_LONG).show()
        else if (destiny == "")
            Toast.makeText(this, "Name of destination can not be empty.", Toast.LENGTH_LONG).show()
        else if (!DATE_PATTERN.matcher(starting).matches() || !DATE_PATTERN.matcher(ending).matches())
            Toast.makeText(
                this,
                "Both dates must be in format:\ndd/mm/yyyy",
                Toast.LENGTH_LONG
            ).show()
        else
            return true
        return false

    }

    fun eraseTrip(position: Int) {
        tripsDB!!.newDao().delete(tripsList.get(position))
        tripsList.remove(tripsList.get(position))
        rvAdapter.notifyItemRemoved(position)
        rvAdapter.notifyItemRangeChanged(position, tripsList.size)
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume called")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop called")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.i("onSaveInstanceState called")
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        Timber.i("onRestoreInstanceState called")
    }
}