package com.example.weather.favourite.view

import com.example.weather.model.FavModel


interface FavClickListeners {
    fun onDeleteClick(favModel: FavModel)
    fun onFavClick(latLon:String)
}