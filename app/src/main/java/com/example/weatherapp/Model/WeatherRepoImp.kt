package com.example.weatherapp.Model

import Welcome
import com.example.weatherapp.network.WeatherRemoteData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherRepoImp(private val weatherRemoteData: WeatherRemoteData):WeatherRepo {
    override suspend fun getCurrentWeather(
        lat: Double?,
        long: Double?,
        apki: String
    ): Flow<Welcome> {
        return flowOf(weatherRemoteData.getCurrentDataResponse(lat,long,apki))
    }

    override suspend fun getForcastWeather(
        lat: Double?,
        long: Double?,
        apki: String
    ): Flow<Forecast> {
        return flowOf(weatherRemoteData.getForcastResponse(lat,long,apki))
    }
    companion object {
        private var instance: WeatherRepoImp? = null
        fun getInstance(remoteSource: WeatherRemoteData): WeatherRepoImp {
            return instance ?: synchronized(this) {
                instance?: WeatherRepoImp(remoteSource)
                    .also { instance = it }
            }
        }
    }
}