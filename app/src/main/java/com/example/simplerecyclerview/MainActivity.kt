package com.example.simplerecyclerview

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.simplerecyclerview.data.DataClass
import com.example.simplerecyclerview.data.DatabaseClass
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_data.view.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    var tripsDB: DatabaseClass? = null
    private lateinit var simpleViewModel: SimpleViewModel
    private var tripsList: ArrayList<DataClass> = ArrayList()
    private lateinit var rvAdapter: SimpleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant()
        tripsDB =
            Room.databaseBuilder(applicationContext, DatabaseClass::class.java, "Trips DB").build()
        Stetho.initializeWithDefaults(this)

        simpleViewModel = ViewModelProviders.of(this).get(SimpleViewModel::class.java)

        simpleRV.layoutManager = LinearLayoutManager(this)
        rvAdapter = SimpleAdapter(tripsList, this)
        simpleRV.adapter = rvAdapter
        addBT.setOnClickListener {
            createPopUp(tripsDB)

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
    fun createPopUp(tripsDB: DatabaseClass?) {
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
            Thread {
                val newTrip = DataClass(
                    popUpInflater.nameOfTripEditText.text.toString(),
                    popUpInflater.destinyAutoCTV.text.toString(),
                    popUpInflater.startDateEditText.text.toString(),
                    popUpInflater.endDateEditText.text.toString()
                )
                tripsList.add(newTrip)
                if (tripsDB != null) {
                    tripsDB.newDao().insert(newTrip)
                }
            }.start()
            rvAdapter.notifyItemInserted(tripsList.size - 1)
            dialog.dismiss()
        }
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