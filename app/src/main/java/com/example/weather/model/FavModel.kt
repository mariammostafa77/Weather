package com.example.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WeatherFavInfo")
data class FavModel(
    @PrimaryKey
    var latLon : String,
    var lat:Double,
    var lon:Double,
    var Favaddress: String,
    val current_clouds: Int,
    val current_humidity: Int,
    val current_pressure: Int,
    val current_temp: Double,
    val dt: Long,
    val current_visibility: Int,
    val current_wind_speed: Double,
    val current_weather_icon:String,
    val current_weather_description:String,
    val current_wind_deg: Int
)
