package com.example.simplerecyclerview.fragments.main_recycler

import android.view.View

interface RV_Methods {
    fun onItemEraseClick(position: Int)

    fun onItemEditClick(position: Int, it: View)
}