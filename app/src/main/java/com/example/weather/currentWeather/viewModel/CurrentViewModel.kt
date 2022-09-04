package com.example.weather.currentWeather.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.LocalCurrentWeatherModel
import com.example.weather.model.RepositoryInterface
import com.example.weather.model.WeatherModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentViewModel(repo: RepositoryInterface) : ViewModel(){
    private val iRepo: RepositoryInterface = repo
    private val currentLocationWeather = MutableLiveData<WeatherModel>()

    //Expose returned online Data
    val onlineWeather: LiveData<WeatherModel> = currentLocationWeather
    fun getCurrentWeather(lat:String,lon:String,lang:String,apiKey:String){
        viewModelScope.launch{
            val weather = iRepo.getCurrentLocationWeather(lat,lon,lang,apiKey)
            withContext(Dispatchers.Main){
                currentLocationWeather.postValue(weather)
            }
        }
    }

    fun insertCurrentWeather(favModel: LocalCurrentWeatherModel) {
        CoroutineScope(Dispatchers.IO).launch {
            iRepo.insertWeather(favModel)
        }
    }

    fun deleteAllWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            iRepo.deleteAll()
        }
    }

    fun getLocalWeather(latLon:String): LiveData<LocalCurrentWeatherModel> {
        return iRepo.getLocalWeather(latLon)
    }


}