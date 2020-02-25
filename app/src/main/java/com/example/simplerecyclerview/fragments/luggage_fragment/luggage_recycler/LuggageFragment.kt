package com.example.simplerecyclerview.fragments.luggage_fragment.luggage_recycler

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerecyclerview.R
import com.example.simplerecyclerview.databinding.FragmentAboutBinding
import com.example.simplerecyclerview.databinding.FragmentLuggageBinding
import com.example.simplerecyclerview.fragments.main_fragment.MainFragment
import kotlinx.android.synthetic.main.fragment_luggage.*

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
        (activity as AppCompatActivity).supportActionBar?.title =
            titleActionBar
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (MainFragment.viewModel!!.luggagesList.size != cardViewPressedPos + 1) {
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
            luggageRV.layoutManager = LinearLayoutManager(activity!!.applicationContext)
            rvAdapter = LuggageAdapter(
                luggagesList, context
            )
            luggageRV.adapter =
                rvAdapter
        }
    }
}