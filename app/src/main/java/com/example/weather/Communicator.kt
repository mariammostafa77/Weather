package com.example.weather

import androidx.fragment.app.Fragment

interface Communicator {
    fun setCurrentLocation(currentAddress:String)
    fun refreshFragment(fragment: Fragment)
}