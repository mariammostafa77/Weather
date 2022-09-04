package com.example.weather.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    companion object {
        var base_url = "https://api.openweathermap.org/data/2.5/"
        fun getRetrofit(): Retrofit? {
            return Retrofit.Builder().baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
    }
}