package com.example.weather.network

import com.example.weather.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("https://api.openweathermap.org/data/2.5/onecall")
    suspend fun getWeather(@Query("lat") lat: String?,
                           @Query("lon") lon: String?,
                           @Query("lang") lang:String,
                           @Query("appid") appid:String?): WeatherModel
}