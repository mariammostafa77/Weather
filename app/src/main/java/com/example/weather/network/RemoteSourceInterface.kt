package com.example.weather.network

import com.example.weather.model.WeatherModel

interface RemoteSourceInterface {
    suspend fun getCurrentWeather(lat:String,lon:String,lang:String,id:String):WeatherModel
}