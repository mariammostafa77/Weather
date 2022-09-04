package com.example.weather.settings.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.weather.*
import com.example.weather.map.view.MapsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess


class SettingFragment : Fragment() {

    private lateinit var rdBtnGroupLocationMode: RadioGroup
    private lateinit var rdBtnGroupLanguage: RadioGroup
    private lateinit var rdBtnGroupTempratureUnit: RadioGroup
    private lateinit var rdBtnGroupWindUnit: RadioGroup
    private lateinit var rdBtnGps:RadioButton
    private lateinit var rdBtnMap:RadioButton
    private lateinit var rdBtnArabic:RadioButton
    private lateinit var rdBtnEnglish:RadioButton
    private lateinit var rdBtnCelsius:RadioButton
    private lateinit var rdBtnMeterSec:RadioButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var communicator: Communicator
    private var currentAddress = ""
    private var strUnitTemp:String=""
    private var strWindUnit:String=""
    private var language : String = "en"


    companion object {
        private const val PERMISSION_ID = 100
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        initComponent(view)
        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor =  sharedPreferences.edit()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        communicator = activity as Communicator

        //check if sharedPreferences exist or not and set default values if not
        if(!sharedPreferences.contains("idCheckedLocation")){
            setDefaultSettings()
        }else{
            rdBtnGroupLocationMode.check(sharedPreferences.getInt("idCheckedLocation", 0))
            rdBtnGroupLanguage.check(sharedPreferences.getInt("idCheckedLanguage", 0))
            rdBtnGroupTempratureUnit.check(sharedPreferences.getInt("idCheckedTempratureUnit", 0))
            rdBtnGroupWindUnit.check(sharedPreferences.getInt("idCheckedWindUnit", 0))
        }
        rdBtnGroupLanguage.setOnCheckedChangeListener { radioGroup, i ->
            val radioLanguage: RadioButton = view.findViewById(i)
            if (radioLanguage.text.toString() == "Arabic") {
                Log.i("TAGLANG", "Arabic")
                language = "ar"
            } else {
                Log.i("TAGLANG", "English")
                language = "en"
            }
            setLanguage(language)

        }
        rdBtnGroupTempratureUnit.setOnCheckedChangeListener { radioGroup, i ->
            val radioTempUnit: RadioButton = view.findViewById(i)
            strUnitTemp = radioTempUnit.text.toString()


        }
        rdBtnGroupWindUnit.setOnCheckedChangeListener { radioGroup, i ->
            val radioWindUnit: RadioButton = view.findViewById(i)
            strWindUnit = radioWindUnit.text.toString()
        }

        /*if(rdBtnGroupLanguage.checkedRadioButtonId !=-1) {
            val radioLanguage: RadioButton = view.findViewById(rdBtnGroupLanguage.checkedRadioButtonId)
            if(radioLanguage.text.toString() == "Arabic"){
                Log.i("TAGLANG","Arabic")
                language="ar"
            }else{
                Log.i("TAGLANG","English")
                language="en"
            }
        }*/

        if(rdBtnGroupTempratureUnit.checkedRadioButtonId !=-1){
            val radioTempUnit: RadioButton = view.findViewById(rdBtnGroupTempratureUnit.checkedRadioButtonId)
            strUnitTemp = radioTempUnit.text.toString()

        }
        if(rdBtnGroupWindUnit.checkedRadioButtonId != -1) {
            val radioWindUnit: RadioButton =
                view!!.findViewById(rdBtnGroupWindUnit.checkedRadioButtonId)
            strWindUnit = radioWindUnit.text.toString()
        }
        onLocationModeGroupListener(view)


        return view
    }

    override fun onStop() {
        super.onStop()
        editor.putInt("idCheckedLocation",rdBtnGroupLocationMode.checkedRadioButtonId)
        editor.putInt("idCheckedLanguage",rdBtnGroupLanguage.checkedRadioButtonId)
        editor.putString("lang",language)
        editor.putInt("idCheckedTempratureUnit",rdBtnGroupTempratureUnit.checkedRadioButtonId)
        editor.putInt("idCheckedWindUnit",rdBtnGroupWindUnit.checkedRadioButtonId)
        editor.putString("language", language)
        editor.putString("tempUnit", strUnitTemp)
        editor.putString("windUnit", strWindUnit)
        editor.apply()
    }

    private fun initComponent(view:View){
        rdBtnGroupLocationMode=view.findViewById(R.id.rdBtnGroupLocationMode)
        rdBtnGroupLanguage=view.findViewById(R.id.rdBtnGroupLanguage)
        rdBtnGroupTempratureUnit=view.findViewById(R.id.rdBtnGroupTempratureUnit)
        rdBtnGroupWindUnit=view.findViewById(R.id.rdBtnGroupWindUnit)
        rdBtnGps=view.findViewById(R.id.rdBtnGps)
        rdBtnMap=view.findViewById(R.id.rdBtnMap)
        rdBtnArabic=view.findViewById(R.id.rdBtnArabic)
        rdBtnEnglish=view.findViewById(R.id.rdBtnEnglish)
        rdBtnCelsius=view.findViewById(R.id.rdBtnCelsius)
        rdBtnMeterSec=view.findViewById(R.id.rdBtnMeterSec)
    }
    private fun setDefaultSettings(){
        rdBtnGps.isChecked=true
        rdBtnCelsius.isChecked=true
        rdBtnMeterSec.isChecked=true
        if(Locale.getDefault().getDisplayLanguage() == "العربية"){
            rdBtnArabic.isChecked=true
            language="ar"
        }else{
            rdBtnEnglish.isChecked=true
            language="en"
        }

    }
    private fun onLocationModeGroupListener(view:View){
        rdBtnGroupLocationMode.setOnCheckedChangeListener { radioGroup, id ->
            val locationRadioBtn: RadioButton = radioGroup.findViewById(id)
            when (locationRadioBtn.text) {
                "Map" -> {
                    if(CheckInternetConnectionFirstTime.checkForInternet(requireContext())){
                        val intent = Intent(requireActivity(), MapsActivity::class.java)
                        val bundle = Bundle()
                        bundle.putInt("key", 1)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }else{
                        val snake = Snackbar.make(
                            view,
                            "You are in offline mode, check your network ",
                            Snackbar.LENGTH_LONG
                        )
                        snake.show()
                        rdBtnGroupLocationMode.check(sharedPreferences.getInt("idCheckedLocation", 0))
                    }

                }
                "GPS" -> {
                    if(CheckInternetConnectionFirstTime.checkForInternet(requireContext())){
                        getCurrentLocation()
                        communicator.setCurrentLocation(currentAddress)
                    }else{
                        val snake = Snackbar.make(
                            view,
                            "You are in offline mode, check your network ",
                            Snackbar.LENGTH_LONG
                        )
                        snake.show()
                        rdBtnGroupLocationMode.check(sharedPreferences.getInt("idCheckedLocation", 0))


                    }


                }
            }
        }
    }
    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID
        )
    }
    private fun showDialogOpenLocationSettings(){
        val builder = AlertDialog.Builder(requireContext())
        //set title for alert dialog
        builder.setTitle(R.string.go_to_location_settings_dialog_title)
        //set message for alert dialog
        builder.setMessage(R.string.go_to_location_settings_dialog_msg)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Allow"){dialogInterface, which ->
            Toast.makeText(requireContext(),"clicked Allow",Toast.LENGTH_LONG).show()
            goToLocationSettings()
        }
        //performing cancel action
        builder.setNegativeButton("Cancel"){dialogInterface , which ->
            Toast.makeText(requireContext(),"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
            requireActivity().finish()
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    private fun getCurrentLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()){ task->
                    val location : Location? = task.result
                    if(location != null){
                        currentAddress = getAddressFromLocation(location.latitude,location.longitude,requireContext())
                        editor.putString("currentLatitude",location.latitude.toString())
                        editor.putString("currentLongitude",location.longitude.toString())
                        editor.putString("currentAddress", currentAddress)
                        editor.apply()

                    }
                }
            }else{
                showDialogOpenLocationSettings()
            }
        }else{
            requestPermission()
            getCurrentLocation()
        }
    }
    private fun isLocationEnabled():Boolean{
        val locationManager : LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun goToLocationSettings(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double, context: Context?): String {
        val addresses: List<Address>
        var address = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            address = addresses[0].getAddressLine(0)
            Toast.makeText(context, address, Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }

    private fun setLanguage(langCode:String){

        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config :Configuration = Configuration()
        config.locale = locale
        requireContext().resources.updateConfiguration(config,requireContext().resources.displayMetrics)
        requireActivity().finish()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)

    }


}