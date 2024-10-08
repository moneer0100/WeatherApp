package com.example.weatherapp

import Welcome
import com.example.weatherapp.Model.Forecast
import com.example.weatherapp.network.WeatherRemoteData


class FakeRemoteDataSource(
    private var currentResponse:Welcome,
   private var forcastRespons:Forecast
): WeatherRemoteData{
    override suspend fun getCurrentDataResponse(
        lat: Double?,
        long: Double?,
        language: String?,
        units: String?
    ): Welcome {
       return currentResponse
    }

    override suspend fun getForcastResponse(
        lat: Double?,
        long: Double?,
        language: String?,
        units: String?
    ): Forecast {
      return forcastRespons
    }
}