package com.example.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather.model.CustomAlert
import com.example.weather.model.FavModel
import com.example.weather.model.LocalCurrentWeatherModel


@Database(entities = [FavModel::class,CustomAlert::class, LocalCurrentWeatherModel::class], version = 4)
abstract class AppDataBase : RoomDatabase() {
    abstract fun weatherDAO(): WeatherDAO

    companion object {
        private var INSTANCE: AppDataBase? = null
        //one thread at a time to access this method
        @Synchronized
        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "weather"
            ).fallbackToDestructiveMigration().build()
        }
    }
}