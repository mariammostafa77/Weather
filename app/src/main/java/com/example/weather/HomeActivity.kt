package com.example.weather

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import com.example.weather.alerts.view.AlertsFragment
import com.example.weather.currentWeather.view.HomeFragment
import com.example.weather.favourite.view.FavouriteFragment
import com.example.weather.settings.view.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.ArrayList

class HomeActivity : AppCompatActivity(),Communicator {

    private val settingFragment= SettingFragment()
    private val homeFragment= HomeFragment()
    private val favouriteFragment= FavouriteFragment()
    private val alertsFragment= AlertsFragment()
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var tvAddress: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var key : Int = 0

    companion object{
        lateinit var oneTimeRequestsArray: ArrayList<OneTimeWorkRequest>
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initComponent()
        oneTimeRequestsArray = ArrayList<OneTimeWorkRequest>()
        val bundle= intent.extras
        if (bundle != null) {
            key=bundle.getInt("key")
        }
        if(key == 1){
            replaceFragment(FavouriteFragment())
            bottomNavigationView.selectedItemId = R.id.fav
        }
        else{
            if(key == 2){
                replaceFragment(AlertsFragment())
                bottomNavigationView.selectedItemId = R.id.alerts
            }else{
                replaceFragment(homeFragment)
            }
        }
        sharedPreferences = this
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    replaceFragment(homeFragment)
                    true
                }
                R.id.alerts -> {
                    replaceFragment(alertsFragment)
                    true
                }
                R.id.fav -> {
                    replaceFragment(favouriteFragment)
                    true
                }
                R.id.settings -> {
                    replaceFragment(settingFragment)
                    true
                }
                else -> false
            }
        }


    }

    override fun onRestart() {
        super.onRestart()
        if(!sharedPreferences.getString("currentAddress", "").isNullOrEmpty()){
            tvAddress.text = sharedPreferences.getString("currentAddress", "")

        }


    }

    private fun initComponent(){
        tvAddress = findViewById (R.id.tvAddress)
        bottomNavigationView=findViewById(R.id.buttomNav)
    }
    private fun replaceFragment(fragment: Fragment){
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout,fragment)
        transaction.commit()
    }

    override fun setCurrentLocation(currentAddress: String) {
        tvAddress.text = currentAddress
    }


    override fun refreshFragment(fragment: Fragment){
        replaceFragment(AlertsFragment())
    }

}
