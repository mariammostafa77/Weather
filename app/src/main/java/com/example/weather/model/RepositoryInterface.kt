package com.example.weather.model

import androidx.lifecycle.LiveData

interface RepositoryInterface {
    suspend fun getCurrentLocationWeather(lat:String,lon:String,lang:String,apiKey:String):WeatherModel


    //Store weather info
    fun insertWeather(weatherInfo: LocalCurrentWeatherModel)
    fun deleteAll()
    fun getLocalWeather(latLon:String): LiveData<LocalCurrentWeatherModel>


    //fav
    val storedFavWeather: LiveData<List<FavModel>>
    fun insertFavWeather(favInfo: FavModel)
    fun deleteFavWeather(favInfo: FavModel)
    fun getFavItemWeather(latLon:String): LiveData<FavModel>

    //alert
    val storedAlertsWeather: LiveData<List<CustomAlert>>
    fun deleteAlertWeather(customAlert: CustomAlert)
    fun insertAlertWeather(id:String, address:String, lon:String, lan:String, dates:String,
                           time:String)


}