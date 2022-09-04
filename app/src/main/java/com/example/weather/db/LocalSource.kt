package com.example.weather.db

import androidx.lifecycle.LiveData
import com.example.weather.model.CustomAlert
import com.example.weather.model.FavModel
import com.example.weather.model.LocalCurrentWeatherModel

interface LocalSource {
    fun insertWeather(weatherModel: LocalCurrentWeatherModel)
    fun deleteAll()
    fun getLocalWeather(latLon:String): LiveData<LocalCurrentWeatherModel>

    fun insertFavWeather(favModel: FavModel)
    val allStoredFavWeather: LiveData<List<FavModel>>
    fun deleteFavInfo(favModel: FavModel)
    fun getFavItemWeather(latLon:String): LiveData<FavModel>

    val allStoredAlertsWeather: LiveData<List<CustomAlert>>
    fun deleteAlertInfo(customAlert: CustomAlert)
    fun insertAlertWeather(customAlert: CustomAlert)

}