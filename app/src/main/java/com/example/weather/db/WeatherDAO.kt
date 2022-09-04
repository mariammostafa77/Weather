package com.example.weather.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Delete
import com.example.weather.model.CustomAlert
import com.example.weather.model.FavModel
import com.example.weather.model.LocalCurrentWeatherModel


@Dao
interface WeatherDAO {
    //Current weather
    @Query("SELECT * From WeatherInfo Where latLon=:latLon")
    fun getLocalWeather(latLon:String): LiveData<LocalCurrentWeatherModel>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWeather(weatherModel: LocalCurrentWeatherModel)
    @Query("DELETE FROM WeatherInfo")
    fun deleteAll()

    //favourite weather
    @get:Query("SELECT * From WeatherFavInfo")
    val getFavWeather: LiveData<List<FavModel>>
    @Query("SELECT * From WeatherFavInfo Where latLon=:latLon")
    fun getFavItemWeather(latLon:String): LiveData<FavModel>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavWeather(favModel: FavModel)
    @Delete
    fun delete(favModel: FavModel)
  /*  @Query("SELECT * From WeatherFavInfo")
    fun getAllFavWeather(): List<FavModel>*/


    //Alert weather
    @get:Query("SELECT * From WeatherAlertInfo")
    val getAlerts: LiveData<List<CustomAlert>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAlertWeather(customAlert: CustomAlert)
    @Delete
    fun deleteAlert(customAlert: CustomAlert)


}