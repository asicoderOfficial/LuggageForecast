package com.example.simplerecyclerview

import com.example.simplerecyclerview.data_luggages.LuggageDataClass
import com.example.simplerecyclerview.fragments.main_fragment.MainFragment
import kotlin.math.max
import kotlin.math.min

class KnapsackLF {

    companion object {
        private var luggageHM: HashMap<String, Int> = HashMap()
        var selectedAction: Int? = null

        private fun initLuggageHM() {
            luggageHM["T-shirts"] = 0
            luggageHM["Jackets"] = 0
            luggageHM["Coat"] = 0
            luggageHM["Long-Sleeved T-Shirts"] = 0
            luggageHM["Shorts"] = 0
            luggageHM["Trousers"] = 0
            luggageHM["Shoes"] = 0
            luggageHM["Underpants"] = 0
            luggageHM["Socks"] = 0
            luggageHM["Umbrella"] = 0
            luggageHM["Raincoat"] = 0
            luggageHM["Hat"] = 0
            luggageHM["Gloves"] = 0
            luggageHM["Scarf"] = 0
        }

        fun solver(weatherForecast: ArrayList<Array<Double>>) {
            initLuggageHM()
            var rains = false
            var tempMin = 100.0
            var tempMax = -100.0
            var mediumTemp = 0.0
            val tripDurationDays = MainFragment.tripDurationDays!! + 1
            for (i in 0 until weatherForecast.size) {
                if (weatherForecast[i][3] in 200.0..531.0 && !rains) {
                    luggageHM["Umbrella"] = 1
                    luggageHM["Raincoat"] = 1
                    rains = true
                }
                mediumTemp += weatherForecast[i][0]
                tempMin = min(tempMin, weatherForecast[i][1])
                tempMax = max(tempMax, weatherForecast[i][2])
            }

            mediumTemp /= 40
            if (mediumTemp < 5) {
                luggageHM["Hat"] = 1
                luggageHM["Gloves"] = 2
                luggageHM["Coat"] = 1
                luggageHM["Trousers"] = (tripDurationDays / 2) + 1
                luggageHM["Scarf"] = 1
                luggageHM["Jacket"] = tripDurationDays / 2
                luggageHM["Long-Sleeved T-Shirts"] = tripDurationDays
            } else if (mediumTemp >= 4 && mediumTemp < 15) {
                luggageHM["Jacket"] = tripDurationDays / 2
                luggageHM["Long-Sleeved T-Shirts"] = tripDurationDays / 2
                luggageHM["Coat"] = 1
                luggageHM["T-shirts"] = tripDurationDays / 2
                luggageHM["Trousers"] = 2
            } else if (mediumTemp >= 15 && mediumTemp < 25) {
                luggageHM["Shorts"] = tripDurationDays / 4
                luggageHM["Jacket"] = 1
                luggageHM["Trousers"] = tripDurationDays / 4
                luggageHM["T-shirts"] = tripDurationDays
            } else {
                luggageHM["T-shirts"] = tripDurationDays
                luggageHM["Shorts"] = tripDurationDays / 2
            }
            luggageHM["Shoes"] = tripDurationDays / 2
            luggageHM["Socks"] = tripDurationDays + 1
            luggageHM["Underpants"] = tripDurationDays + 1

            val newLuggage = LuggageDataClass(
                MainFragment.cardViewPosition!!,
                luggageHM["T-shirts"],
                luggageHM["Jacket"],
                luggageHM["Coat"],
                luggageHM["Long-Sleeved T-Shirts"],
                luggageHM["Shorts"],
                luggageHM["Trousers"],
                luggageHM["Shoes"],
                luggageHM["Underpants"],
                luggageHM["Socks"],
                luggageHM["Umbrella"],
                luggageHM["Raincoat"],
                luggageHM["Hat"],
                luggageHM["Gloves"],
                luggageHM["Scarf"]
            )
            if (selectedAction == 0) { //adding new luggage
                MainFragment.viewModel!!.luggagesDB.insert(newLuggage)
                MainFragment.viewModel!!.luggagesList.add(newLuggage)
            } else { //editing existing luggage
                MainFragment.viewModel!!.luggagesDB.update(newLuggage)
                MainFragment.viewModel!!.luggagesList[MainFragment.cardViewPosition!!] = newLuggage
            }
        }
    }
}