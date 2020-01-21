package com.example.simplerecyclerview.fragments

import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import com.example.simplerecyclerview.R
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerecyclerview.JsonParserService
import com.example.simplerecyclerview.data_trips.TripsDataClass
import com.example.simplerecyclerview.data_trips.TripsDatabaseClass
import com.example.simplerecyclerview.fragments.main_recycler.MainAdapter
import com.example.simplerecyclerview.fragments.main_recycler.RV_Methods
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.popup_data.view.*
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    var tripsDB: TripsDatabaseClass? = null
    private var tripsList: ArrayList<TripsDataClass> = ArrayList()
    private var citiesList = ArrayList<String>()
    private var citiesIdMap = HashMap<String, String>()
    private var intentJsonParserService: Intent? = null

    private lateinit var rvAdapter: MainAdapter
    private val DATE_PATTERN: Pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}")
    private val MILLIES_DAY = 86400000
    private var formate =
        SimpleDateFormat("DD/MM/YYYY", Locale.getDefault())

    @RequiresApi(Build.VERSION_CODES.N)
    @Override
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.plant()
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            view!!.findNavController()
        )
                || super.onOptionsItemSelected(item)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            Stetho.initializeWithDefaults(context)
            mainRV.layoutManager = LinearLayoutManager(activity!!.applicationContext)
            bufferer()
            intentJsonParserService = Intent(context, JsonParserService::class.java)
            tripsDB = TripsDatabaseClass.getAppDataBase(activity!!.applicationContext)

            rvAdapter =
                MainAdapter(
                    tripsList,
                    context,
                    object :
                        RV_Methods {
                        override fun onItemEditClick(position: Int, it: View) {
                            editTripPopUp(position, it)
                        }

                        override fun onItemEraseClick(position: Int) {
                            eraseTrip(position)
                        }
                    })

            if (tripsList.isEmpty() && tripsDB!!.newDao().getNumberOfTrips() != 0)
                tripsList.addAll(tripsDB!!.newDao().getAllTrips() as ArrayList<TripsDataClass>)

            mainRV.adapter = rvAdapter
            val autoCompleteAdapter =
                ArrayAdapter(
                    activity!!.applicationContext,
                    android.R.layout.simple_list_item_1,
                    citiesList
                )
            addBT.setOnClickListener {
                addTripPopUp(autoCompleteAdapter, it)
            }
        } catch (e: Exception) {
            println()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addTripPopUp(autoCompleteAdapter: ArrayAdapter<String>, it: View) {
        val popUpInflater = layoutInflater.inflate(R.layout.popup_data, null, false)
        popUpInflater.startDateTV.text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(
            System.currentTimeMillis()
        )
        popUpInflater.endDateTV.text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(
            System.currentTimeMillis()
        )
        popUpInflater.startDateTV.setOnClickListener {
            showDatePicker(popUpInflater, 1)
        }
        popUpInflater.endDateTV.setOnClickListener {
            showDatePicker(popUpInflater, 2)
        }
        popUpInflater.destinyAutoCTV.threshold = 0
        popUpInflater.destinyAutoCTV.setAdapter(autoCompleteAdapter)

        val popUpBuilder = AlertDialog.Builder(it.context)
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
                    popUpInflater.endDateTV.text.toString(),
                    0
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
                sCityID = citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()]!!
                tripDurationDays =
                    ((getDestinationTime(popUpInflater.endDateTV.text.toString()) - getDestinationTime(
                        popUpInflater.startDateTV.text.toString()
                    )) / MILLIES_DAY).toString()
                rvAdapter.notifyItemInserted(tripsList.size)
                //startService(intentJsonParserService)
                dialog.dismiss()
            }
            //JsonParserService.weatherGetter(citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()]!!)
        }
        Toast.makeText(context, tripsDB!!.newDao().getNumberOfTrips().toString(), Toast.LENGTH_LONG)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun editTripPopUp(position: Int, it: View) {
        val popUpInflater = layoutInflater.inflate(R.layout.popup_data, null, false)
        popUpInflater.nameOfTripEditText.setText(tripsList[position].name)
        popUpInflater.destinyAutoCTV.setText(tripsList[position].destinationName)
        popUpInflater.startDateTV.text = (tripsList[position].start)
        popUpInflater.endDateTV.text = (tripsList[position].end)

        val popUpBuilder = AlertDialog.Builder(it.context)
        popUpBuilder.setView(popUpInflater)
        popUpBuilder.setCancelable(false)
        popUpBuilder.setPositiveButton("SAVE") { _: DialogInterface, _: Int -> }
        val dialog: AlertDialog =
            popUpBuilder.setNegativeButton("CANCEL") { _: DialogInterface, _: Int -> }
                .create()
        popUpInflater.startDateTV.setOnClickListener {
            showDatePicker(popUpInflater, 1)
        }
        popUpInflater.endDateTV.setOnClickListener {
            showDatePicker(popUpInflater, 2)
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (!textChecker(
                    popUpInflater.nameOfTripEditText.text.toString(),
                    popUpInflater.destinyAutoCTV.text.toString(),
                    popUpInflater.startDateTV.text.toString(),
                    popUpInflater.endDateTV.text.toString(),
                    1

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
                sCityID = citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()]!!
                tripDurationDays =
                    ((getDestinationTime(popUpInflater.endDateTV.text.toString()) - getDestinationTime(
                        popUpInflater.startDateTV.text.toString()
                    )) / MILLIES_DAY).toString()
                //startService(intentJsonParserService)
                Toast.makeText(context, sCityID, Toast.LENGTH_LONG).show()
                rvAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }
    }

    fun eraseTrip(position: Int) {
        tripsDB!!.newDao().delete(tripsList[position])
        tripsList.remove(tripsList[position])
        rvAdapter.notifyItemRemoved(position)
        rvAdapter.notifyItemRangeChanged(position, tripsList.size)
    }

    companion object {
        var sCityID: String? = null
        var tripDurationDays: String? = null
    }

    private fun textChecker(
        name: String,
        destiny: String,
        starting: String,
        ending: String,
        actionSelected: Int
    ): Boolean {
        val dateStartingInMillies = Date(getDestinationTime(starting))
        val dateEndingInMillies = Date(getDestinationTime(ending))
        val diffStartEnd = dateEndingInMillies.time - dateStartingInMillies.time
        if (name == "")
            Toast.makeText(context, "Name of trip can not be empty.", Toast.LENGTH_LONG).show()
        else if (!citiesIdMap.containsKey(destiny))
            Toast.makeText(context, "Destination does not exist.", Toast.LENGTH_LONG).show()
        else if (!DATE_PATTERN.matcher(starting).matches()
            || !DATE_PATTERN.matcher(ending).matches()
        )
            Toast.makeText(
                context,
                "Both dates must be in format:\ndd/mm/yyyy",
                Toast.LENGTH_LONG
            ).show()
        else if (dateEndingInMillies.before(dateStartingInMillies))
            Toast.makeText(context, "Trip can not end before it starts.", Toast.LENGTH_LONG).show()
        else if (diffStartEnd > 1296000000) {
            Toast.makeText(
                context,
                "The trip can not last more than 15 days, because weather forecast is not available.",
                Toast.LENGTH_LONG
            ).show()
        } else if (actionSelected == 0) {
            for (i in 0 until tripsList.size) {
                if (name == tripsList[i].name) {
                    Toast.makeText(
                        activity,
                        "You can not have trips with the same name.",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
            }
            return true
        }
        return false
    }

    private fun getDestinationTime(dateDestination: String): Long {
        val unixTimeDestination = Calendar.getInstance()
        unixTimeDestination.set(
            dateDestination.substring(6, 9).toInt(),
            dateDestination.substring(3, 4).toInt(),
            dateDestination.substring(0, 1).toInt(),
            0, 0, 0
        )
        return unixTimeDestination.timeInMillis
    }

    private fun bufferer() {

        val bfr = BufferedReader(InputStreamReader(activity!!.assets.open("city_id.txt")))
        bfr.forEachLine {
            val pair = it.split(" ")
            citiesIdMap[pair[1]] = pair[0]
        }
        citiesList = ArrayList(citiesIdMap.keys)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker(popupInflater: View, i: Int) {
        val currently = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            popupInflater.context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val date = formate.format(selectedDate.time)

                when (i) {
                    1 -> popupInflater.startDateTV.text = date
                    2 -> popupInflater.endDateTV.text = date
                }
            },
            currently.get(Calendar.YEAR),
            currently.get(Calendar.MONTH),
            currently.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}