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
import kotlinx.android.synthetic.main.popup_data.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var tripsDB: DatabaseClass? = null
    private var tripsList: ArrayList<DataClass> = ArrayList()
    private lateinit var popUpInflater: View
    private lateinit var rvAdapter: SimpleAdapter

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
                tripPopUp(1, position)
            }

            override fun onItemEraseClick(position: Int) {
                eraseTrip(position)
            }
        })
        tripsList.addAll(tripsDB!!.newDao().getAllTrips() as ArrayList<DataClass>)

        simpleRV.adapter = rvAdapter

        addBT.setOnClickListener {
            tripPopUp(0, tripsList.size)
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


    @SuppressLint("InflateParams")
    fun tripPopUp(actionPopUp: Int, position: Int) {
        val popUpInflater = layoutInflater.inflate(R.layout.popup_data, null, false)
        popUpInflater.startDateEditText.setText(SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis()))
        popUpInflater.endDateEditText.setText(SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis()))
        val popUpBuilder = AlertDialog.Builder(this)
        popUpBuilder.setView(popUpInflater)
        popUpBuilder.setCancelable(false)
        popUpBuilder.setPositiveButton("CREATE") { dialogInterface: DialogInterface, i: Int -> }
        val dialog: AlertDialog =
            popUpBuilder.setNegativeButton("CANCEL") { dialogInterface: DialogInterface, i: Int -> }
                .create()
        dialog.show()
        when (actionPopUp) {
            0 -> addTripPopUp(dialog)
            1 -> editTripPopUp(dialog, position)
        }
    }

    fun addTripPopUp(dialog: AlertDialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
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

    fun editTripPopUp(dialog: AlertDialog, position: Int) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            Thread {
                val newTrip = DataClass(
                    popUpInflater.nameOfTripEditText.text.toString(),
                    popUpInflater.destinyAutoCTV.text.toString(),
                    popUpInflater.startDateEditText.text.toString(),
                    popUpInflater.endDateEditText.text.toString()
                )
                tripsList.set(position, newTrip)
                tripsDB?.newDao()?.update(newTrip)
                rvAdapter.notifyDataSetChanged()
            }.start()
        }
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