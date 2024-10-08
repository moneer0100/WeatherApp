package com.example.weatherapp.Model

import Welcome
import kotlinx.coroutines.flow.Flow

interface WeatherRepo
{
    suspend fun getCurrentWeather(
        lat:Double?,
        long: Double?,
        language: String,
        units: String?
    ):Flow<Welcome>
    suspend fun getForcastWeather(
        lat:Double?,
        long: Double?,
        language: String,
        units: String?
    ):Flow<Forecast>



    //fav
fun getAllWeatherFavoirit():Flow<List<FaviouritWeather>>
suspend fun insertDataFaviourit(fav:FaviouritWeather)
suspend fun deletWeatherFav(fav: FaviouritWeather)

///////alert
    fun getAlert():Flow<List<AlertPojo>>
    suspend fun insertAlert(alert:AlertPojo)
    suspend fun deleteAlert(alert:AlertPojo)
    fun getAlertWithId(id: String): AlertPojo
}