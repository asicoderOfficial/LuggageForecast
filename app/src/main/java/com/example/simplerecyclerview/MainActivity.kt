package com.example.simplerecyclerview

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.simplerecyclerview.data_trips.TripsDataClass
import com.example.simplerecyclerview.data_trips.TripsDatabaseClass
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_data.*
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
    private var citiesList = ArrayList<String>()
    private var citiesIdMap = HashMap<String, String>()

    private lateinit var rvAdapter: SimpleAdapter

    private val DATE_PATTERN: Pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}")
    private var formate = SimpleDateFormat("dd MMM, YYYY", Locale.US)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant()
        Stetho.initializeWithDefaults(this)
        simpleRV.layoutManager = LinearLayoutManager(this)

        bufferer()

        tripsDB = TripsDatabaseClass.getAppDataBase(this)

        rvAdapter = SimpleAdapter(tripsList, this, object : RV_Methods {
            override fun onItemEditClick(position: Int) {
                editTripPopUp(position)
            }

            override fun onItemEraseClick(position: Int) {
                eraseTrip(position)
            }
        })

        tripsList.addAll(tripsDB!!.newDao().getAllTrips() as ArrayList<TripsDataClass>)

        simpleRV.adapter = rvAdapter
        val autoCompleteAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, citiesList)
        addBT.setOnClickListener {
            addTripPopUp(autoCompleteAdapter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addTripPopUp(autoCompleteAdapter: ArrayAdapter<String>) {
        val popUpInflater = layoutInflater.inflate(R.layout.popup_data, null, false)
        popUpInflater.startDateTV.text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(
            System.currentTimeMillis()
        )
        popUpInflater.endDateTV.text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(
            System.currentTimeMillis()
        )
        popUpInflater.startDateTV.setOnClickListener {
            popUpInflater.startDateTV.text = showDatePicker()
        }
        popUpInflater.endDateTV.setOnClickListener {
            popUpInflater.endDateTV.text = showDatePicker()
        }
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
                    popUpInflater.startDateTV.text.toString(),
                    popUpInflater.endDateTV.text.toString()
                )
            )
                dialog.dismiss()
            else {
                Thread {
                    val newTrip = TripsDataClass(
                        popUpInflater.nameOfTripEditText.text.toString(),
                        popUpInflater.destinyAutoCTV.text.toString(),
                        citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()]!!,
                        popUpInflater.startDateTV.text.toString(),
                        popUpInflater.endDateTV.text.toString()
                    )
                    tripsList.add(newTrip)

                    tripsDB?.newDao()?.insert(newTrip)
                }.start()
                rvAdapter.notifyItemInserted(tripsList.size)
                dialog.dismiss()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun editTripPopUp(position: Int) {
        val popUpInflater = layoutInflater.inflate(R.layout.popup_data, null, false)
        popUpInflater.nameOfTripEditText.setText(tripsList[position].name)
        popUpInflater.destinyAutoCTV.setText(tripsList[position].destinationName)
        popUpInflater.startDateTV.text = (tripsList[position].start)
        popUpInflater.endDateTV.text = (tripsList[position].end)
        val popUpBuilder = AlertDialog.Builder(this)
        popUpBuilder.setView(popUpInflater)
        popUpBuilder.setCancelable(false)
        popUpBuilder.setPositiveButton("SAVE") { _: DialogInterface, _: Int -> }
        val dialog: AlertDialog =
            popUpBuilder.setNegativeButton("CANCEL") { _: DialogInterface, _: Int -> }
                .create()
        popUpInflater.startDateTV.setOnClickListener {
            popUpInflater.startDateTV.text = showDatePicker()
        }
        popUpInflater.endDateTV.setOnClickListener {
            popUpInflater.endDateTV.text = showDatePicker()
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (!textChecker(
                    popUpInflater.nameOfTripEditText.text.toString(),
                    popUpInflater.destinyAutoCTV.text.toString(),
                    popUpInflater.startDateTV.text.toString(),
                    popUpInflater.endDateTV.text.toString()
                )
            )
                dialog.dismiss()
            else {
                Thread {
                    val newTrip = TripsDataClass(
                        popUpInflater.nameOfTripEditText.text.toString(),
                        popUpInflater.destinyAutoCTV.text.toString(),
                        citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()]!!,
                        popUpInflater.startDateTV.text.toString(),
                        popUpInflater.endDateTV.text.toString()
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
        val dateStarting = Date(starting)
        val dateEnding = Date(ending)
        if (name == "")
            Toast.makeText(this, "Name of trip can not be empty.", Toast.LENGTH_LONG).show()
        else if (!citiesIdMap.containsKey(destiny))
            Toast.makeText(this, "Destination does not exist.", Toast.LENGTH_LONG).show()
        else if (!DATE_PATTERN.matcher(starting).matches()
            || !DATE_PATTERN.matcher(ending).matches()
        )
            Toast.makeText(
                this,
                "Both dates must be in format:\ndd/mm/yyyy",
                Toast.LENGTH_LONG
            ).show()
        else if (dateEnding.before(dateStarting))
            Toast.makeText(this, "Trip can not end before it starts.", Toast.LENGTH_LONG).show()
        else
            return true
        return false

    }

    private fun bufferer() {
        val bfr = BufferedReader(InputStreamReader(assets.open("city_id.txt")))
        bfr.forEachLine {
            val pair = it.split(" ")
            citiesIdMap[pair[1]] = pair[0]
        }
        citiesList = ArrayList(citiesIdMap.keys)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker(): String? {
        val currently = Calendar.getInstance()
        var date: String? = null
        val datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                date = formate.format(selectedDate.time)
            },
            currently.get(Calendar.YEAR),
            currently.get(Calendar.MONTH),
            currently.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
        return date
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