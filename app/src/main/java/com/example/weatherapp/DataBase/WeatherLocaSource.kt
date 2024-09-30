package com.example.weatherapp.DataBase

import com.example.weatherapp.Model.FaviouritWeather
import kotlinx.coroutines.flow.Flow

interface WeatherLocaSource {
    fun getAllWeather(): Flow<List<FaviouritWeather>>
    suspend fun insertWeather(favWeather: FaviouritWeather)
    suspend fun deleteWeather(favWeather: FaviouritWeather)
}