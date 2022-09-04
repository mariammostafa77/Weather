package com.example.weather.favourite.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weather.db.ConcreteLocalSource
import com.example.weather.R
import com.example.weather.favourite.viewModel.FavouriteViewModel
import com.example.weather.favourite.viewModel.FavouriteViewModelFactory
import com.example.weather.model.FavModel
import com.example.weather.model.Repository
import com.example.weather.network.WeatherClient
import java.lang.IllegalStateException

class FavInfoDialog(var latLon : String) : DialogFragment() {
    private lateinit var tvFavAddress : TextView
    private lateinit var tvFavDate : TextView
    private lateinit var tvFavTime : TextView
    private lateinit var tvFavTemp : TextView
    private lateinit var tvFavdesc : TextView
    private lateinit var tvFavIcon : ImageView
    private lateinit var tvFavHumidity : TextView
    private lateinit var tvFavVisibility : TextView
    private lateinit var tvFavCloud : TextView
    private lateinit var tvFavPressure : TextView
    private lateinit var tvFavWindSpeed : TextView
    private lateinit var tvFavWindDeg : TextView
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var favouriteViewModelFactory: FavouriteViewModelFactory
    private lateinit var sharedPreferences: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val alertDialog=AlertDialog.Builder(it)
            val view = requireActivity().layoutInflater.inflate(R.layout.fav_location_info,null)
            initComponent(view)

            favouriteViewModelFactory = FavouriteViewModelFactory(
                Repository.getInstance(
                    WeatherClient.getInstance(),
                    ConcreteLocalSource(requireContext()),requireContext()))
            favouriteViewModel = ViewModelProvider(this, favouriteViewModelFactory).get(FavouriteViewModel::class.java)
            favouriteViewModel.getFavItemWeather(latLon).observe(this) { favInfo ->
                if (favInfo != null)
                    convertToSelectedUnit(favInfo)
                    tvFavAddress.text = favInfo.Favaddress
                    val dateTime=java.time.format.DateTimeFormatter.ISO_INSTANT
                        .format(java.time.Instant.ofEpochSecond(favInfo.dt))
                    val delim = "T"
                    val dateTimeList = dateTime.split(delim)
                    tvFavDate.text=dateTimeList[0]
                    val delim2 = "Z"
                    val timeList = dateTimeList[1].split(delim2)
                    tvFavTime.text=timeList[0]
                    tvFavdesc.text=favInfo.current_weather_description
                    tvFavHumidity.text=favInfo.current_humidity.toString()
                    tvFavVisibility.text=favInfo.current_visibility.toString()
                    tvFavCloud.text=favInfo.current_clouds.toString()
                    tvFavPressure.text=favInfo.current_pressure.toString()
                    tvFavWindDeg.text = favInfo.current_wind_deg.toString()
                    Glide.with(requireContext())
                        .load("https://openweathermap.org/img/w/${favInfo.current_weather_icon}.png")
                        .into(tvFavIcon)
            }
            alertDialog.setView(view)


            /*alertDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
            })*/
            alertDialog.create()
        }?:throw IllegalStateException("Activity is null")
    }
    fun initComponent(view:View){
        tvFavAddress = view.findViewById(R.id.tvFavAddress)
        tvFavDate = view.findViewById(R.id.tvFavDate)
        tvFavTime = view.findViewById(R.id.tvFavTime)
        tvFavTemp = view.findViewById(R.id.tvFavTemp)
        tvFavdesc = view.findViewById(R.id.tvFavdesc)
        tvFavIcon = view.findViewById(R.id.tvFavIcon)
        tvFavHumidity = view.findViewById(R.id.tvFavHumidity)
        tvFavVisibility = view.findViewById(R.id.tvFavVisibility)
        tvFavCloud = view.findViewById(R.id.tvFavCloud)
        tvFavPressure = view.findViewById(R.id.tvFavPressure)
        tvFavWindSpeed = view.findViewById(R.id.tvFavWindSpeed)
        tvFavWindDeg = view.findViewById(R.id.tvFavWindDeg)
        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    }
    @SuppressLint("SetTextI18n")
    private fun convertToSelectedUnit(favModel: FavModel){
        when (sharedPreferences.getString("tempUnit","")){
            "Fahrenheit (F°)"->tvFavTemp.text=((1.8* ((favModel.current_temp).minus(273)).plus(32).toInt()).toInt()).toString()+getString(
                R.string.Fahrenheit2)
            "Celsius (C°)"->tvFavTemp.text=(((favModel.current_temp).minus(273.15).toInt())).toString()+getString(
                R.string.Celsius2)
            "Kelvin (K°)"->tvFavTemp.text=((favModel.current_temp).toInt()).toString()+getString(
                R.string.Kelvin2)
            else->tvFavTemp.text=(((favModel.current_temp).minus(273.15).toInt())).toString()+getString(
                R.string.Celsius2)
        }
        when (sharedPreferences.getString("windUnit","")){
            "miles/hour"->tvFavWindSpeed.text=((2.23694*(favModel.current_wind_speed).toInt()).toInt()).toString()
            else->tvFavWindSpeed.text=(favModel.current_wind_speed.toInt()).toString()
        }
    }
}