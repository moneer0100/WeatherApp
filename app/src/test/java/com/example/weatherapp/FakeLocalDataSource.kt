package com.example.weatherapp

import com.example.weatherapp.DataBase.WeatherLocaSource
import com.example.weatherapp.Model.AlertPojo
import com.example.weatherapp.Model.FaviouritWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource(
    private var favList:MutableList<FaviouritWeather> = mutableListOf(),
    private var allertList:MutableList<AlertPojo> = mutableListOf()
): WeatherLocaSource{

    override fun getAllWeather(): Flow<List<FaviouritWeather>> {
    return flowOf(favList)
    }

    override suspend fun insertWeather(favWeather: FaviouritWeather) {
  favList.add(favWeather)
    }

    override suspend fun deleteWeather(favWeather: FaviouritWeather) {
     favList.remove(favWeather)
    }

    override fun getAlert(): Flow<List<AlertPojo>> {
       return  flowOf(allertList)
    }

    override suspend fun insertAlert(alert: AlertPojo) {
      allertList.add(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        allertList.remove(alert)
    }

    override fun getAlertWithId(id: String): AlertPojo {
     return allertList.find { it.id==id }?:throw NoSuchElementException("No AlertPojo found with ID: $id")
    }
}