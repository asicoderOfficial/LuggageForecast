package com.example.simplerecyclerview.fragments.luggage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerecyclerview.R
import com.example.simplerecyclerview.databinding.FragmentLuggageBinding
import com.example.simplerecyclerview.fragments.main.MainFragment
import com.example.simplerecyclerview.fragments.luggage.recycler.LuggageAdapter
import kotlinx.android.synthetic.main.fragment_luggage.*
import java.lang.StringBuilder

/**
 * Fragment that displays luggage and enables its sharing.
 *
 * @author Asicoder
 */
class LuggageFragment : Fragment() {

    private lateinit var binding: FragmentLuggageBinding

    companion object {
        var cardViewPressedPos = 0
        var titleActionBar = ""
        lateinit var rvAdapter: LuggageAdapter
        val luggagesList = arrayListOf<Pair<String, Int>>()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_luggage, container, false)
        luggagesList.clear()
        (activity as AppCompatActivity).supportActionBar?.title =
            titleActionBar
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Wait until the luggage is ready to be visualized, otherwise it would throw KNPE.
        if (MainFragment.viewModel?.luggagesList?.size != cardViewPressedPos + 1) {
            Toast.makeText(context, "Please wait until your luggage is prepared", Toast.LENGTH_LONG)
                .show()
        } else {
            val selectedLuggage = MainFragment.viewModel!!.luggagesList[cardViewPressedPos]
            luggagesList.addAll(
                listOf(
                    Pair("T-shirts", selectedLuggage.t_shirts!!),
                    Pair("Jackets", selectedLuggage.jacket!!),
                    Pair("Coat", selectedLuggage.coat!!),
                    Pair("Long-sleeved T-Shirts", selectedLuggage.long_sleevedT_shirts!!),
                    Pair("Shorts", selectedLuggage.shorts!!),
                    Pair("Trousers", selectedLuggage.trousers!!),
                    Pair("Shoes", selectedLuggage.shoes!!),
                    Pair("Underpants", selectedLuggage.underpants!!),
                    Pair("Socks", selectedLuggage.socks!!),
                    Pair("Umbrella", selectedLuggage.umbrella!!),
                    Pair("Raincoat", selectedLuggage.raincoat!!),
                    Pair("Hat", selectedLuggage.hat!!),
                    Pair("Gloves", selectedLuggage.gloves!!),
                    Pair("Scarf", selectedLuggage.scarf!!)
                )
            )
            luggageRV.layoutManager = LinearLayoutManager(activity?.applicationContext)
            rvAdapter =
                LuggageAdapter(
                    luggagesList, context
                )
            luggageRV.adapter =
                rvAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.share_luggage_menu, menu)
    }

    /**
     * Method to handle sharing luggage's information.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.toString() == "Share") {
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.type = "text/plain"
            val shareMessage = StringBuilder()
            shareMessage.append("This is my luggage:\n")
            luggagesList.forEach {
                shareMessage.append(it.first).append(" : ").append(it.second).append("\n")
            }
            intentShare.putExtra(Intent.EXTRA_TEXT, shareMessage.toString())
            startActivity(Intent.createChooser(intentShare, "Share your luggage!"))
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}