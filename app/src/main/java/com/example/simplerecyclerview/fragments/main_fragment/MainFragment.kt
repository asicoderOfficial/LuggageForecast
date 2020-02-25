package com.example.simplerecyclerview.fragments.main_fragment

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerecyclerview.JsonParserService
import com.example.simplerecyclerview.KnapsackLF
import com.example.simplerecyclerview.R
import com.example.simplerecyclerview.data_luggages.LuggageDataClass
import com.example.simplerecyclerview.data_luggages.LuggageDatabaseClass
import com.example.simplerecyclerview.data_trips.TripsDataClass
import com.example.simplerecyclerview.data_trips.TripsDatabaseClass
import com.example.simplerecyclerview.databinding.ActivityMainBinding
import com.example.simplerecyclerview.databinding.FragmentMainBinding
import com.example.simplerecyclerview.fragments.main_fragment.main_recycler.MainAdapter
import com.example.simplerecyclerview.fragments.main_fragment.main_recycler.RV_Methods
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.popup_data.view.*
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private var tripsDB: TripsDatabaseClass? = null
    var tripsList: ArrayList<TripsDataClass> = ArrayList()
    private var intentJsonParserService: Intent? = null
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private lateinit var alarmManager: AlarmManager
    private lateinit var viewModel: MainViewModel
    private lateinit var rvAdapter: MainAdapter
    private val MILLIES_DAY = 86400000
    private var formate =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private lateinit var binding: FragmentMainBinding

    companion object {
        var sCityID: String? = null
        var tripDurationDays: Int? = null
        var cardViewPosition: Int? = null
        var luggagesList: ArrayList<LuggageDataClass> = ArrayList()
        var luggageDB: LuggageDatabaseClass? = null
        var citiesIdMap = HashMap<String, String>()
        var citiesList = ArrayList<String>()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.plant()
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, null, false)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        return binding.root
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
            Stetho.initializeWithDefaults(context)
            mainRV.layoutManager = LinearLayoutManager(activity!!.applicationContext)
            intentJsonParserService = Intent(context, JsonParserService::class.java)
        alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            tripsDB = TripsDatabaseClass.getAppDataBase(activity!!.applicationContext)
            luggageDB = LuggageDatabaseClass.getAppDataBase(activity!!.applicationContext)

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
                            showDeleteDialog(position)
                        }
                    }, activity!!
                )

            /*luggageDB!!.luggagesDao().deleteAllLuggages()
            tripsDB!!.newDao().deleteAllTrips()t.clear()
            tripsList.clear()*/

        if (citiesList.isEmpty())
            bufferer()
            if (tripsList.isEmpty() && tripsDB!!.newDao().getNumberOfTrips() != 0)
                tripsList.addAll(tripsDB!!.newDao().getAllTrips() as ArrayList<TripsDataClass>)
            if (luggageDB!!.luggagesDao().getNumberOfLuggages() != 0)
                luggagesList.addAll(
                    luggageDB!!.luggagesDao().getAllLugages() as ArrayList<LuggageDataClass>
                )

            mainRV.adapter = rvAdapter
            autoCompleteAdapter =
                ArrayAdapter(
                    view.context,
                    android.R.layout.simple_list_item_1,
                    citiesList
                )
            addBT.setOnClickListener {
                addTripPopUp(autoCompleteAdapter, it)
            }
    }

    private fun showDeleteDialog(position: Int) {
        val popupInflater = layoutInflater.inflate(R.layout.popup_erase, null, false)
        val popUpBuilder = AlertDialog.Builder(view!!.context)
        popUpBuilder.setView(popupInflater)
        popUpBuilder.setCancelable(false)
        popUpBuilder.setPositiveButton("YES") { _: DialogInterface, _: Int -> }
        val dialog: AlertDialog =
            popUpBuilder.setNegativeButton("NO") { _: DialogInterface, _: Int -> }.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            eraseTrip(position)
            dialog.dismiss()
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            dialog.dismiss()
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
            while (!textChecker(
                    popUpInflater.nameOfTripEditText.text.toString(),
                    popUpInflater.destinyAutoCTV.text.toString(),
                    popUpInflater.startDateTV.text.toString(),
                    popUpInflater.endDateTV.text.toString(),
                    0
                )
            )
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
                ((viewModel.getDestinationTime(
                    popUpInflater.endDateTV.text.toString()
                ) - viewModel.getDestinationTime(
                    popUpInflater.startDateTV.text.toString()
                )) / MILLIES_DAY).toInt()
            rvAdapter.notifyItemInserted(tripsList.size)
            cardViewPosition = tripsList.size - 1
            KnapsackLF.selectedAction = 0
            activity!!.startService(intentJsonParserService)
            dialog.dismiss()
        }
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
        popUpInflater.destinyAutoCTV.threshold = 0
        popUpInflater.destinyAutoCTV.setAdapter(autoCompleteAdapter)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (textChecker(
                    popUpInflater.nameOfTripEditText.text.toString(),
                    popUpInflater.destinyAutoCTV.text.toString(),
                    popUpInflater.startDateTV.text.toString(),
                    popUpInflater.endDateTV.text.toString(),
                    1
                )
            ) {
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
                    ((viewModel.getDestinationTime(
                        popUpInflater.endDateTV.text.toString()
                    ) - viewModel.getDestinationTime(
                        popUpInflater.startDateTV.text.toString()
                    )) / MILLIES_DAY).toInt() + 1
                cardViewPosition = position
                KnapsackLF.selectedAction = 1
                activity!!.startService(intentJsonParserService)
                rvAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }
    }

    private fun eraseTrip(position: Int) {
        tripsDB!!.newDao().delete(tripsList[position])
        tripsList.remove(tripsList[position])
        luggageDB!!.luggagesDao().delete(
            luggagesList[position]
        )
        luggagesList.removeAt(position)
        rvAdapter.notifyItemRemoved(position)
        rvAdapter.notifyItemRangeChanged(position, tripsList.size)
    }

    private fun bufferer() {
        val bfr = BufferedReader(InputStreamReader(activity!!.assets.open("city_id.txt")))
        bfr.forEachLine {
            val pair = it.split(" ")
            MainFragment.citiesIdMap[pair[1]] = pair[0]
        }
        citiesList = ArrayList(MainFragment.citiesIdMap.keys)
    }

    fun textChecker(
        name: String,
        destiny: String,
        starting: String,
        ending: String,
        actionSelected: Int
    ): Boolean {
        val dateStartingInMillies = Date(
            viewModel.getDestinationTime(
                starting
            )
        )
        val dateEndingInMillies = Date(
            viewModel.getDestinationTime(
                ending
            )
        )
        val diffStartEnd = dateEndingInMillies.time - dateStartingInMillies.time
        if (name == "")
            Toast.makeText(context, "Name of trip can not be empty.", Toast.LENGTH_LONG).show()
        else if (!citiesIdMap.containsKey(destiny))
            Toast.makeText(context, "Destination does not exist.", Toast.LENGTH_LONG).show()
        else if (dateEndingInMillies.before(dateStartingInMillies))
            Toast.makeText(context, "Trip can not end before it starts.", Toast.LENGTH_LONG).show()
        else if (diffStartEnd > 345600000) {
            Toast.makeText(
                context,
                "The trip can not last more than 5 days, because weather forecast is not available at larger intervals of time.",
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
        } else {
            return true
        }
        return false
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