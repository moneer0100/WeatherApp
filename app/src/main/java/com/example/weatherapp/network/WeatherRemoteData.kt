package com.example.weatherapp.network

import Welcome
import com.example.weatherapp.Model.Forecast

interface WeatherRemoteData {
    suspend fun getCurrentDataResponse(

        lat:Double?,
        long: Double?,
        apikey:String?

    ):Welcome
    suspend fun getForcastResponse(

        lat:Double?,
        long: Double?,
        apikey:String?
    ): Forecast
}
