package com.example.weather.model

import androidx.room.Embedded
import androidx.room.Entity

data class WeatherModel(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val minutely: List<Minutely>,
    val timezone: String,
    val timezone_offset: Int,
    var alerts:ArrayList<Alerts>
)