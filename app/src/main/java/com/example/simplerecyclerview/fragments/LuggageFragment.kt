package com.example.simplerecyclerview.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplerecyclerview.R
import kotlinx.android.synthetic.main.fragment_luggage.view.*


class LuggageFragment : Fragment() {

    companion object {
        var cardViewPressedPos = 0
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflated = inflater.inflate(R.layout.fragment_luggage, container, false)
        val selectedLuggage = MainFragment.luggagesList[cardViewPressedPos]
        inflated.luggageTextView.text = "T-shirts -> ${selectedLuggage.t_shirts}\n" +
                "Jackets -> ${selectedLuggage.jacket}\n" +
                "Coat -> ${selectedLuggage.coat}\n" +
                "Long-sleeved T-Shirts -> ${selectedLuggage.long_sleevedT_shirts}\n" +
                "Shorts -> ${selectedLuggage.shorts}\n" +
                "Trousers -> ${selectedLuggage.trousers}\n" +
                "Shoes -> ${selectedLuggage.shoes}\n" +
                "Underpants -> ${selectedLuggage.underpants}\n" +
                "Socks -> ${selectedLuggage.socks}\n" +
                "Umbrella -> ${selectedLuggage.umbrella}\n" +
                "Raincoat -> ${selectedLuggage.raincoat}\n" +
                "Hat -> ${selectedLuggage.hat}\n" +
                "Gloves -> ${selectedLuggage.gloves}\n" +
                "Scarf -> ${selectedLuggage.scarf}\n"
        return inflated
    }


}
