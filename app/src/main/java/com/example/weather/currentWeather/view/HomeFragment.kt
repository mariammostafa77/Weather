package com.example.weather.currentWeather.view


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.db.ConcreteLocalSource
import com.example.weather.*
import com.example.weather.R
import com.example.weather.currentWeather.viewModel.CurrentViewModel
import com.example.weather.currentWeather.viewModel.CurrentViewModelFactory
import com.example.weather.model.LocalCurrentWeatherModel
import com.example.weather.model.Repository
import com.example.weather.network.WeatherClient
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var currentAddress = ""
    private lateinit var communicator: Communicator
    private lateinit var currentViewModel: CurrentViewModel
    private lateinit var currentViewModelFactory: CurrentViewModelFactory
    private lateinit var currentIcon: ImageView
    private lateinit var tvTemp: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvVisibility: TextView
    private lateinit var tvCloud: TextView
    private lateinit var tvPressure: TextView
    private lateinit var tvWindSpeed: TextView
    private lateinit var wind_deg: TextView
    private lateinit var tvOffline:TextView
    private lateinit var cardHourly:CardView
    private lateinit var cardDaily:CardView
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var recycleHourly: RecyclerView
    private lateinit var hourlyLinearManager: LinearLayoutManager
    private lateinit var recycleDay: RecyclerView
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var daysLinearLManager: LinearLayoutManager
    private lateinit var internetConnectionChecker: InternetConnectionChecker

    private var lat = ""
    private var lon = ""
    private var lang = ""
    private val apiKey= "68dea5913ee5edc56461d63440681c6c"


    companion object {
        private const val PERMISSION_ID = 100

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initComponent(view)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor =  sharedPreferences.edit()
        communicator = activity as Communicator

        internetConnectionChecker = InternetConnectionChecker(requireContext())
        internetConnectionChecker.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                currentViewModel.getCurrentWeather(lat,lon,lang,apiKey)

            } else {
                val snake = Snackbar.make(
                    view,
                    "Ops! You Lost internet connection!!!",
                    Snackbar.LENGTH_LONG
                )
                snake.show()
            }
        }
        currentViewModel.onlineWeather.observe(requireActivity()){ currentWeather->
            convertToSelectedUnit(currentWeather.current.temp,currentWeather.current.wind_speed)
            daysAdapter.setUpdatedData(currentWeather.daily,requireContext(),sharedPreferences.getString("tempUnit","").toString(),sharedPreferences.getString("windUnit","").toString())
            hourlyAdapter.setUpdatedData(currentWeather.hourly,requireContext(),sharedPreferences.getString("tempUnit","").toString())
            val dateTime=java.time.format.DateTimeFormatter.ISO_INSTANT
                .format(java.time.Instant.ofEpochSecond(currentWeather.current.dt))
            val delim = "T"
            val dateTimeList = dateTime.split(delim)
            tvDate.text=dateTimeList[0]
            val delim2 = "Z"
            val timeList = dateTimeList[1].split(delim2)
            tvTime.text=timeList[0]
            tvDesc.text= currentWeather.current.weather[0].description
            tvHumidity.text=currentWeather.current.humidity.toString()
            tvVisibility.text=currentWeather.current.visibility.toString()
            tvCloud.text=currentWeather.current.clouds.toString()
            tvPressure.text=currentWeather.current.pressure.toString()
            wind_deg.text=currentWeather.current.wind_deg.toString()
            Glide.with(requireContext())
                .load("https://openweathermap.org/img/w/${currentWeather.current.weather[0].icon}.png")
                .into(currentIcon)
            LoadingScreen.hideLoading()
            val myModel = LocalCurrentWeatherModel(
                ("$lat$lon"),
                lat.toDouble(),
                lon.toDouble(),
                getAddressFromLocation(lat.toDouble(), lon.toDouble(),requireContext()),
                currentWeather.current.clouds,
                currentWeather.current.humidity,
                currentWeather.current.pressure,
                currentWeather.current.temp,
                currentWeather.current.dt,
                currentWeather.current.visibility,
                currentWeather.current.wind_speed,
                currentWeather.current.weather[0].icon,
                currentWeather.current.weather[0].description,
                currentWeather.current.wind_deg,dateTimeList[0],
                timeList[0]
            )
            currentViewModel.deleteAllWeather()
            currentViewModel.insertCurrentWeather(myModel)
            cardHourly.visibility=View.VISIBLE
            cardDaily.visibility=View.VISIBLE
            recycleDay.visibility=View.VISIBLE
            tvOffline.visibility=View.INVISIBLE
        }



        return view
    }

    private fun initComponent(view: View) {
        currentIcon=view.findViewById(R.id.currentIcon)
        tvHumidity=view.findViewById(R.id.tvHumidity)
        tvVisibility=view.findViewById(R.id.tvVisibility)
        tvCloud=view.findViewById(R.id.tvCloud)
        tvPressure=view.findViewById(R.id.tvPressure)
        tvWindSpeed=view.findViewById(R.id.tvWindSpeed)
        wind_deg=view.findViewById(R.id.tvWind)
        tvTemp=view.findViewById(R.id.tvTemp)
        tvDesc=view.findViewById(R.id.tvDesc)
        tvDate=view.findViewById(R.id.tvDate)
        tvTime=view.findViewById(R.id.tvTime)
        tvOffline=view.findViewById(R.id.tvOffline)
        cardHourly=view.findViewById(R.id.cardHourly)
        cardDaily=view.findViewById(R.id.cardDaily)
        recycleHourly=view.findViewById(R.id.recycleHourly)
        hourlyLinearManager= LinearLayoutManager(requireContext())
        hourlyLinearManager.setOrientation(LinearLayoutManager.HORIZONTAL)
        recycleHourly.setLayoutManager(hourlyLinearManager)
        hourlyAdapter= HourlyAdapter()
        recycleHourly.setAdapter(hourlyAdapter)
        recycleDay=view.findViewById(R.id.recycleDay)
        daysLinearLManager= LinearLayoutManager(requireContext())
        daysLinearLManager.setOrientation(LinearLayoutManager.VERTICAL)
        recycleDay.setLayoutManager(daysLinearLManager)
        daysAdapter= DaysAdapter()
        recycleDay.setAdapter(daysAdapter)
        currentViewModelFactory = CurrentViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),requireActivity()))

        currentViewModel = ViewModelProvider(this, currentViewModelFactory).get(CurrentViewModel::class.java)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if(sharedPreferences.getString("currentAddress", "").isNullOrEmpty()){
            getCurrentLocation()
        }else{
            currentAddress = sharedPreferences.getString("currentAddress", "")!!
            communicator.setCurrentLocation(currentAddress)
            lat = sharedPreferences.getString("currentLatitude","")!!
            lon = sharedPreferences.getString("currentLongitude","")!!
            lang=sharedPreferences.getString("language","")!!
            setLanguage(lang)

            LoadingScreen.displayLoadingWithText(requireContext(),"Loading...",false)
            if(CheckInternetConnectionFirstTime.checkForInternet(requireContext())){
                currentViewModel.getCurrentWeather(lat,lon,lang,apiKey)
            }else{
                currentViewModel.getLocalWeather("$lat$lon").observe(viewLifecycleOwner){
                    if(it != null){
                        convertToSelectedUnit(it.temp,it.wind_speed)
                        tvCloud.text=it.clouds.toString()
                        tvHumidity.text=it.humidity.toString()
                        tvVisibility.text=it.visibility.toString()
                        tvPressure.text=it.pressure.toString()
                        wind_deg.text=it.wind_deg.toString()
                        tvDesc.text= it.weather_desc
                        tvTime.text=it.time
                        tvDate.text=it.date
                        Glide.with(requireContext())
                            .load("https://openweathermap.org/img/w/${it.weather_icon}.png")
                            .into(currentIcon)
                        LoadingScreen.hideLoading()
                    }
                    cardHourly.visibility=View.INVISIBLE
                    cardDaily.visibility=View.INVISIBLE
                    recycleDay.visibility=View.INVISIBLE
                    tvOffline.visibility=View.VISIBLE

                }
            }
        }
    }

    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    ==PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    ==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
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
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }

    private fun setLanguage(langCode:String){
        val locale  = Locale(langCode)
        Locale.setDefault(locale)
        val config  = Configuration()
        config.locale = locale
        requireContext().resources.updateConfiguration(config,requireContext().resources.displayMetrics)

    }
    @SuppressLint("SetTextI18n")
    private fun convertToSelectedUnit(temp:Double,wind_speed:Double){
        when (sharedPreferences.getString("tempUnit","")){
            "Fahrenheit (F°)"->tvTemp.text=((1.8* (temp.minus(273)).plus(32).toInt()).toInt()).toString()+getString(
                R.string.Fahrenheit2)
            "Celsius (C°)"->tvTemp.text=((temp.minus(273.15).toInt())).toString()+getString(
                R.string.Celsius2)
            "Kelvin (K°)"->tvTemp.text=(temp.toInt()).toString()+getString(
                R.string.Kelvin2)
            else->tvTemp.text=(((temp).minus(273.15).toInt())).toString()+getString(
                R.string.Celsius2)
        }
        when (sharedPreferences.getString("windUnit","")){
            "miles/hour"->tvWindSpeed.text=((2.23694*(wind_speed).toInt()).toInt()).toString()
            else->tvWindSpeed.text=(wind_speed.toInt()).toString()
        }
    }

}