package com.example.simplerecyclerview.fragments.main

import android.app.DatePickerDialog
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
import com.example.simplerecyclerview.data.luggages.LuggageDatabaseClass
import com.example.simplerecyclerview.data.trips.TripsDataClass
import com.example.simplerecyclerview.data.trips.TripsDatabaseClass
import com.example.simplerecyclerview.databinding.FragmentMainBinding
import com.example.simplerecyclerview.fragments.main.recycler.MainAdapter
import com.example.simplerecyclerview.fragments.main.recycler.RVMethods
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.popup_data.view.*
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Fragment Main's class.
 * MVVM.
 * It handles the following events:
 * -Adding a trip
 * -Editing a trip
 * -Showing trips
 * -Showing luggage
 * And storage of trips and luggages.
 *
 * @author Asicoder
 */

class MainFragment : Fragment() {

    private var intentJsonParserService: Intent? = null
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private val MILLIES_DAY = 86400000
    private var formate =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private lateinit var binding: FragmentMainBinding

    companion object {
        var sCityID: String? = null
        var tripDurationDays: Int? = null
        var cardViewPosition: Int? = null
        var citiesIdMap = HashMap<String, String>()
        var citiesList = ArrayList<String>()
        lateinit var rvAdapter: MainAdapter
        var viewModel: MainViewModel? = null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.plant()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        val application = requireNotNull(this.activity).application
        val tripsDataSource = TripsDatabaseClass.getAppDataBase(application)!!.newDao()
        val luggagesDataSource = LuggageDatabaseClass.getAppDataBase(application)!!.luggagesDao()
        val viewModelFactory =
            MainViewModelFactory(tripsDataSource, luggagesDataSource, application)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        setHasOptionsMenu(true)
        binding.lifecycleOwner = this
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
        mainRV.layoutManager = LinearLayoutManager(activity?.applicationContext)

        //Declaration of the Intent that will get and parse json.
        intentJsonParserService = Intent(context, JsonParserService::class.java)

        rvAdapter =
            activity?.let {
                viewModel?.tripsList?.let { it1 ->
                    MainAdapter(
                        it1,
                        context,
                        object :
                            RVMethods {
                            override fun onItemEditClick(position: Int, it: View) {
                                editTripPopUp(position, it)
                            }

                            override fun onItemEraseClick(position: Int) {
                                showDeleteDialog(position)
                            }
                        }, it
                    )
                }
            }!!
        //Get the list of all cities without duplicates, only the first time the app is launched.
        if (citiesList.isEmpty())
            bufferer()

        //Main recyclerview's adapter.
        mainRV.adapter = rvAdapter

        //Adapter for the AutoCTV to make sure the user selects a valid name of destination.
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

    /**
     * Function to show confirmation dialog after the rubbish bin icon is clicked,
     * to make sure the user does want to erase the trip.
     */
    private fun showDeleteDialog(position: Int) {
        val popupInflater = layoutInflater.inflate(R.layout.popup_erase, null, false)
        val popUpBuilder = view?.context?.let { AlertDialog.Builder(it) }
        popUpBuilder?.setView(popupInflater)
        popUpBuilder?.setCancelable(false)
        popUpBuilder?.setPositiveButton("YES") { _: DialogInterface, _: Int -> }
        val dialog: AlertDialog =
            popUpBuilder?.setNegativeButton("NO") { _: DialogInterface, _: Int -> }!!.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            viewModel?.eraseTrip(position)
            dialog.dismiss()
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            dialog.dismiss()
        }
    }

    /**
     * Function to add a new trip to the database.
     * Shows the dialog,
     * checks until everything is alright with textChecker function,
     * and then add the trip to the database, with the ID of destination.
     */
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
            if ((textChecker(
                    popUpInflater.nameOfTripEditText.text.toString(),
                    popUpInflater.destinyAutoCTV.text.toString(),
                    popUpInflater.startDateTV.text.toString(),
                    popUpInflater.endDateTV.text.toString(),
                    0
                ))
            ) {
                Thread {
                    val newTrip =
                        citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()]?.let { it1 ->
                            TripsDataClass(
                                popUpInflater.nameOfTripEditText.text.toString(),
                                popUpInflater.destinyAutoCTV.text.toString(),
                                it1,
                                popUpInflater.startDateTV.text.toString(),
                                popUpInflater.endDateTV.text.toString()
                            )
                        }
                    //tripsList.add(newTrip)
                    //viewModel.tripsDB.insert(newTrip)
                    newTrip?.let { it1 -> viewModel?.addTrip(it1) }
                }.start()
                sCityID = citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()]
                tripDurationDays =
                    ((viewModel!!.getDestinationTime(
                        popUpInflater.endDateTV.text.toString()
                    ) - viewModel!!.getDestinationTime(
                        popUpInflater.startDateTV.text.toString()
                    )) / MILLIES_DAY).toInt()
                viewModel?.tripsList?.size?.let { it1 -> rvAdapter.notifyItemInserted(it1) }
                cardViewPosition = viewModel!!.tripsList.size - 1
                //To determine that we are adding, not editing
                KnapsackLF.selectedAction = 0
                activity?.startService(intentJsonParserService)
                dialog.dismiss()
            }
        }
    }

    /**
     * Function that displays the dialog with the corresponding data.
     * Once textChecker returns true, it saves the new changes.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun editTripPopUp(position: Int, it: View) {
        val popUpInflater = layoutInflater.inflate(R.layout.popup_data, null, false)
        popUpInflater.nameOfTripEditText.setText(viewModel?.tripsList?.get(position)?.name)
        popUpInflater.destinyAutoCTV.setText(viewModel?.tripsList?.get(position)?.destinationName)
        popUpInflater.startDateTV.text = (viewModel?.tripsList?.get(position)?.start)
        popUpInflater.endDateTV.text = (viewModel?.tripsList?.get(position)?.end)

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
                    val newTrip =
                        citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()]?.let { it1 ->
                            TripsDataClass(
                                popUpInflater.nameOfTripEditText.text.toString(),
                                popUpInflater.destinyAutoCTV.text.toString(),
                                it1,
                                popUpInflater.startDateTV.text.toString(),
                                popUpInflater.endDateTV.text.toString()
                            )
                        }
                    //tripsList[position] = newTrip
                    //tripsDB?.newDao()?.update(newTrip)
                    newTrip?.let { it1 -> viewModel?.updateTrip(it1, position) }
                }.start()
                sCityID = citiesIdMap[popUpInflater.destinyAutoCTV.text.toString()]
                tripDurationDays =
                    ((viewModel!!.getDestinationTime(
                        popUpInflater.endDateTV.text.toString()
                    ) - viewModel!!.getDestinationTime(
                        popUpInflater.startDateTV.text.toString()
                    )) / MILLIES_DAY).toInt() + 1
                cardViewPosition = position
                //To determine that an existing trip has been edited.
                KnapsackLF.selectedAction = 1
                activity?.startService(intentJsonParserService)
                rvAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }
    }

    /**
     * Function to get all trips from the file city_id.txt without repeated names.
     * Only called the first time the app starts.
     */
    private fun bufferer() {
        val bfr = BufferedReader(InputStreamReader(activity?.assets?.open("city_id.txt")))
        bfr.forEachLine {
            val pair = it.split(" ")
            citiesIdMap[pair[1]] = pair[0]
        }
        citiesList = ArrayList(citiesIdMap.keys)
    }

    /**
     * Function to check that none of the following conditions succeed:
     * -There's no name for the trip.
     * -The destination does not exist.
     * -The trips lasts more than 5 days.
     * -There are two trips with the same name.
     */
    private fun textChecker(
        name: String,
        destiny: String,
        starting: String,
        ending: String,
        actionSelected: Int
    ): Boolean {
        val dateStartingInMillies = viewModel?.getDestinationTime(
            starting
        )?.let {
            Date(
                it
            )
        }
        val dateEndingInMillies = viewModel?.getDestinationTime(
            ending
        )?.let {
            Date(
                it
            )
        }
        val diffStartEnd = dateEndingInMillies!!.time - dateStartingInMillies!!.time
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
            for (i in 0 until viewModel!!.tripsList.size) {
                if (name == viewModel!!.tripsList[i].name) {
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

    /**
     * Shows the DatePicker when you click the textview to introduce the begin or end of the trip.
     */
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