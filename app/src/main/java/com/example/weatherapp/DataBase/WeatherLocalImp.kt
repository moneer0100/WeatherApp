package com.example.weatherapp.DataBase

import com.example.weatherapp.Model.FaviouritWeather
import kotlinx.coroutines.flow.Flow

class WeatherLocalImp(private val dao: Dao):WeatherLocaSource {

    companion object {
        @Volatile
        var instance: WeatherLocalImp? = null
        fun getInstance(weatherDao: Dao): WeatherLocalImp {
            return instance?: synchronized(this){
                instance?: WeatherLocalImp(weatherDao)
                    .also { instance = it }
            }
        }
    }
    override fun getAllWeather(): Flow<List<FaviouritWeather>> {
     return dao.getFavoriteWeather()
    }

    override suspend fun insertWeather(favWeather: FaviouritWeather) {
      return dao.insertWeather(favWeather)
    }

    override suspend fun deleteWeather(favWeather: FaviouritWeather) {
        return dao.deleteWeather(favWeather)
    }
}