package com.example.weather.network

import com.example.weather.model.WeatherModel

class WeatherClient private constructor() : RemoteSourceInterface{

    companion object{
        private var instance: WeatherClient? = null
        fun getInstance(): WeatherClient{
            return  instance?: WeatherClient()
        }
    }


    override suspend fun getCurrentWeather(lat:String,lon:String,lang:String,id:String): WeatherModel {
        val weatherService = RetrofitHelper.getRetrofit()?.create(WeatherService::class.java)
        val response = weatherService?.getWeather(lat,lon,lang,id)
        return response!!
    }
}