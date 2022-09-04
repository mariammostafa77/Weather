package com.example.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "WeatherAlertInfo")
data class CustomAlert(
    @PrimaryKey
    var id:String,
    var address:String,
    var lon:String,
    var lan:String,
    var dates:String,
    var time:String
)
