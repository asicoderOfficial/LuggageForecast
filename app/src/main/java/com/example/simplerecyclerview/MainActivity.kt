package com.example.simplerecyclerview

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.simplerecyclerview.data.DataClass
import com.example.simplerecyclerview.data.DatabaseClass
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.new_item.view.*
import kotlinx.android.synthetic.main.popup_data.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var tripsDB: DatabaseClass? = null
    private var tripsList: ArrayList<DataClass> = ArrayList()
    private lateinit var rvAdapter: SimpleAdapter
    private val DATE_PATTERN: Pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant()
        Stetho.initializeWithDefaults(this)
        simpleRV.layoutManager = LinearLayoutManager(this)

        tripsDB =
            Room.databaseBuilder(applicationContext, DatabaseClass::class.java, "Trips DB")
                .allowMainThreadQueries().build()

        rvAdapter = SimpleAdapter(tripsList, this, object : RV_Methods {
            override fun onItemEditClick(position: Int) {
                editTripPopUp(position)
            }

            override fun onItemEraseClick(position: Int) {
                eraseTrip(position)
            }
        })
        tripsList.addAll(tripsDB!!.newDao().getAllTrips() as ArrayList<DataClass>)

        simpleRV.adapter = rvAdapter

        addBT.setOnClickListener {
            addTripPopUp()
        }
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

    fun addTripPopUp() {
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
                    val newTrip = DataClass(
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
                    val newTrip = DataClass(
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