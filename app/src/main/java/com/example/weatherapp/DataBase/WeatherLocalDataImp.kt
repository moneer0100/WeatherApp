package com.example.weatherapp.DataBase

import Welcome
import com.example.weatherapp.Model.AlertPojo
import com.example.weatherapp.Model.FaviouritWeather
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataImp(private val dao: Dao):WeatherLocaSource {

    companion object {
        @Volatile
        var instance: WeatherLocalDataImp? = null
        fun getInstance(dao: Dao): WeatherLocalDataImp {
            return instance?: synchronized(this){
                instance?: WeatherLocalDataImp(dao)
                    .also { instance = it }
            }
        }
    }



///////////fav
    override fun getAllWeather(): Flow<List<FaviouritWeather>> {
     return dao.getFavoriteWeather()
    }

    override suspend fun insertWeather(favWeather: FaviouritWeather) {
      return dao.insertWeather(favWeather)
    }

    override suspend fun deleteWeather(favWeather: FaviouritWeather) {
        return dao.deleteWeather(favWeather)
    }
    //////alert

    override fun getAlert(): Flow<List<AlertPojo>> {
        return dao.getAlert()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
     return dao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
   return dao.deleteAlert(alert)
    }

    override fun getAlertWithId(id: String): AlertPojo {
        return dao.getAlertWithId(id)
    }
}