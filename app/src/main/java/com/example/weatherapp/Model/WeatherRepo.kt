package com.example.weatherapp.Model

import Welcome
import kotlinx.coroutines.flow.Flow

interface WeatherRepo
{
    suspend fun getCurrentWeather(
        lat:Double?,
        long: Double?,
        apki:String
    ):Flow<Welcome>
    suspend fun getForcastWeather(
        lat:Double?,
        long: Double?,
        apki:String
    ):Flow<Forecast>

    fun getAllWeatherFavoirit():Flow<List<FaviouritWeather>>
suspend fun insertDataFaviourit(fav:FaviouritWeather)
suspend fun deletWeatherFav(fav: FaviouritWeather)
}