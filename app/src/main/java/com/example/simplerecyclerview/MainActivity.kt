package com.example.simplerecyclerview

import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerecyclerview.data.Dao
import com.example.simplerecyclerview.data.DataClass
import com.example.simplerecyclerview.data.DatabaseClass
import com.facebook.stetho.Stetho
import io.reactivex.Single

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_data.*
import kotlinx.android.synthetic.main.popup_data.view.*

class MainActivity : AppCompatActivity() {

    private var db: DatabaseClass? = null
    private var data: Dao? = null

    private lateinit var simpleViewModel: SimpleViewModel
    private var tripsList: ArrayList<Trip> = ArrayList()
    private lateinit var rvAdapter: SimpleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDB()
        Stetho.initializeWithDefaults(this)
        simpleViewModel = ViewModelProviders.of(this).get(SimpleViewModel::class.java)

        simpleRV.layoutManager = LinearLayoutManager(this)
        rvAdapter = SimpleAdapter(tripsList, this)
        simpleRV.adapter = rvAdapter
        addBT.setOnClickListener {
            createPopUp()

        }

    }

    fun createPopUp() {
        val popUpBuilder = AlertDialog.Builder(this)
        val popUpInflater = layoutInflater.inflate(R.layout.popup_data, null, false)
        popUpBuilder.setView(popUpInflater)
        popUpBuilder.setCancelable(false)
        popUpBuilder.setPositiveButton("CREATE") { dialogInterface: DialogInterface, i: Int -> }
        val dialog: AlertDialog =
            popUpBuilder.setNegativeButton("CANCEL") { dialogInterface: DialogInterface, i: Int -> }
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newTrip = Trip(
                popUpInflater.nameOfTripEditText.text.toString(),
                popUpInflater.destinyAutoCTV.text.toString(),
                popUpInflater.startDateEditText.text.toString(),
                popUpInflater.endDateEditText.text.toString()
            )
            tripsList.add(newTrip)
            rvAdapter.notifyItemInserted(tripsList.size - 1)
            dialog.dismiss()
        }


        /*newItemCreator.show()
        newItemCreator.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newTrip = DataClass(popUpInflater.nameOfTripEditText.text.toString(),
                                    popUpInflater.destinyAutoCTV.text.toString(),
                                    popUpInflater.startDateEditText.text.toString(),
                                    popUpInflater.endDateEditText.text.toString())
            simpleViewModel.insertNewTrip(newTrip)
            //this?.insert(newTrip)
            newItemCreator.dismiss()
        }
        newItemCreator.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            newItemCreator.dismiss()
        }*/

    }


    fun initDB() {
        db = DatabaseClass.getAppDataBase(this)
        data = db?.newDao()
    }
}
