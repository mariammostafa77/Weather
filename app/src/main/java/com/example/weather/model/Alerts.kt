package com.example.weather.model

data class Alerts(
    var sender_name:String,
    var event: String,
    var start: Int,
    var end: Int,
    var description:String,
    var tags:ArrayList<String>
)
