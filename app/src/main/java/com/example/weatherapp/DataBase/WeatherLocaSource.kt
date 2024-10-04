package com.example.weatherapp.DataBase

import Welcome
import com.example.weatherapp.Model.AlertPojo
import com.example.weatherapp.Model.FaviouritWeather
import kotlinx.coroutines.flow.Flow

interface WeatherLocaSource {

    ///fav
    fun getAllWeather(): Flow<List<FaviouritWeather>>
    suspend fun insertWeather(favWeather: FaviouritWeather)
    suspend fun deleteWeather(favWeather: FaviouritWeather)

    //////alert
    fun getAlert():Flow<List<AlertPojo>>
    suspend fun insertAlert(alert:AlertPojo)
    suspend fun deleteAlert(alert:AlertPojo)
    fun getAlertWithId(id: String): AlertPojo
}