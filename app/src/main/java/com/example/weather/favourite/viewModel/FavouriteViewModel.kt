package com.example.weather.favourite.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.FavModel
import com.example.weather.model.RepositoryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavouriteViewModel(
    private val _irepo: RepositoryInterface
) : ViewModel() {

    fun insertFav(favModel: FavModel) {
        CoroutineScope(Dispatchers.IO).launch {
            _irepo.insertFavWeather(favModel)
        }
    }

    fun deleteFavWeatherInfo(favLoc: FavModel) {
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.deleteFavWeather(favLoc)
        }
    }

    fun localFavInfo():LiveData<List<FavModel>> {
        return _irepo.storedFavWeather
    }
    fun getFavItemWeather(latLon:String): LiveData<FavModel>{
        return _irepo.getFavItemWeather(latLon)
    }

}

