package com.example.simplerecyclerview.fragments.main.recycler

import android.view.View

/**
 * Interface implemented by MainAdapter class to handle erasing and editing trips.
 *
 * @author Asicoder
 */
interface RVMethods {
    fun onItemEraseClick(position: Int)

    fun onItemEditClick(position: Int, it: View)
}