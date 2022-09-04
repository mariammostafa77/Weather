package com.example.weather.db

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weather.model.CustomAlert
import com.example.weather.model.FavModel
import com.example.weather.model.LocalCurrentWeatherModel


class  ConcreteLocalSource (context: Context
) : LocalSource {
    private val dao: WeatherDAO?
    override fun insertWeather(weatherModel: LocalCurrentWeatherModel) {
        dao?.insertWeather(weatherModel)
    }

    override fun deleteAll() {
        dao?.deleteAll()
    }

    override fun getLocalWeather(latLon: String): LiveData<LocalCurrentWeatherModel> {
        Log.i("TAG","from local source ${dao?.getLocalWeather(latLon)?.value?.address}")

        return dao?.getLocalWeather(latLon)!!
    }

    override val allStoredFavWeather: LiveData<List<FavModel>>
    override fun deleteFavInfo(favModel: FavModel) {
        dao?.delete(favModel)
    }

    override fun getFavItemWeather(latLon: String): LiveData<FavModel> {
        return dao?.getFavItemWeather(latLon)!!
    }
    override val allStoredAlertsWeather: LiveData<List<CustomAlert>>
    override fun deleteAlertInfo(customAlert: CustomAlert) {
        dao?.deleteAlert(customAlert)
    }

    override fun insertAlertWeather(customAlert: CustomAlert) {
        dao?.insertAlertWeather(customAlert)
    }

    override fun insertFavWeather(favModel: FavModel) {
        dao?.insertFavWeather(favModel)
    }

    init {
        val db: AppDataBase = AppDataBase.getInstance(context)
        dao = db.weatherDAO()
        allStoredFavWeather = dao.getFavWeather
        allStoredAlertsWeather=dao.getAlerts
    }



}