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
import com.example.simplerecyclerview.data_cities.CitiesDatabaseClass
import com.example.simplerecyclerview.data_trips.TripsDataClass
import com.example.simplerecyclerview.data_trips.TripsDatabaseClass
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_data.view.*
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var tripsDB: TripsDatabaseClass? = null
    private var tripsList: ArrayList<TripsDataClass> = ArrayList()

    private var citiesDB: CitiesDatabaseClass? = null
    private var citiesList = ArrayList<String>()
    private var citiesIdMap = HashMap<String, String>()

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
            CitiesDatabaseClass.getAppDataBase(applicationContext)

        rvAdapter = SimpleAdapter(tripsList, this, object : RV_Methods {
            override fun onItemEditClick(position: Int) {
                editTripPopUp(position)
            }

            override fun onItemEraseClick(position: Int) {
                eraseTrip(position)
            }
        })

        tripsList.addAll(tripsDB!!.newDao().getAllTrips() as ArrayList<TripsDataClass>)

        val bfr = BufferedReader(InputStreamReader(assets.open("city_id.txt")))
        bfr.forEachLine {
            val pair = it.split(" ")
            citiesList.add(pair[1])
            citiesIdMap.put(pair[1], pair[0])
        }

        simpleRV.adapter = rvAdapter
        val autoCompleteAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, citiesList)
        addBT.setOnClickListener {
            addTripPopUp(autoCompleteAdapter)
        }
    }

    private fun addTripPopUp(autoCompleteAdapter: ArrayAdapter<String>) {
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
        popUpInflater.destinyAutoCTV.threshold = 0
        popUpInflater.destinyAutoCTV.setAdapter(autoCompleteAdapter)

        val popUpBuilder = AlertDialog.Builder(this)
        popUpBuilder.setView(popUpInflater)
        popUpBuilder.setCancelable(false)
        popUpBuilder.setPositiveButton("CREATE") { _: DialogInterface, _: Int -> }
        val dialog: AlertDialog =
            popUpBuilder.setNegativeButton("CANCEL") { _: DialogInterface, _: Int -> }
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
                        citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()].toString(),
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
        popUpInflater.nameOfTripEditText.setText(tripsList[position].name)
        popUpInflater.destinyAutoCTV.setText(tripsList[position].destination)
        popUpInflater.startDateEditText.setText((tripsList[position].start))
        popUpInflater.endDateEditText.setText((tripsList[position].end))
        val popUpBuilder = AlertDialog.Builder(this)
        popUpBuilder.setView(popUpInflater)
        popUpBuilder.setCancelable(false)
        popUpBuilder.setPositiveButton("CREATE") { _: DialogInterface, _: Int -> }
        val dialog: AlertDialog =
            popUpBuilder.setNegativeButton("CANCEL") { _: DialogInterface, _: Int -> }
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
                    tripsList[position] = newTrip
                    tripsDB?.newDao()?.update(newTrip)
                }.start()
                rvAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }
    }

    private fun textChecker(
        name: String,
        destiny: String,
        starting: String,
        ending: String
    ): Boolean {
        if (name == "")
            Toast.makeText(this, "Name of trip can not be empty.", Toast.LENGTH_LONG).show()
        else if (!citiesIdMap.containsKey(destiny))
            Toast.makeText(this, "Destination does not exist.", Toast.LENGTH_LONG).show()
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
        tripsDB!!.newDao().delete(tripsList[position])
        tripsList.remove(tripsList[position])
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