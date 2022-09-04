package com.example.weather.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "WeatherInfo")
data class LocalCurrentWeatherModel(
    @PrimaryKey
    val latLon : String,
    val lat:Double,
    val lon:Double,
    val address: String,
    val clouds: Int,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val dt: Long,
    val visibility: Int,
    val wind_speed: Double,
    val weather_icon:String,
    val weather_desc:String,
    val wind_deg: Int,
    val date: String,
    val time:String,

)
