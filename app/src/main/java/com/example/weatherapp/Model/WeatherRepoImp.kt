package com.example.weatherapp.Model

import Welcome
import com.example.weatherapp.DataBase.WeatherLocaSource
import com.example.weatherapp.DataBase.WeatherLocalImp
import com.example.weatherapp.network.WeatherRemoteData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherRepoImp(
    private val weatherRemoteData: WeatherRemoteData
    ,private val weatherLocalData:WeatherLocaSource
):WeatherRepo {
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
        fun getInstance(remoteSource: WeatherRemoteData,localSource: WeatherLocaSource): WeatherRepoImp {
            return instance ?: synchronized(this) {
                instance?: WeatherRepoImp(remoteSource,localSource)
                    .also { instance = it }
            }
        }


    }

    override fun getAllWeatherFavoirit(): Flow<List<FaviouritWeather>> {
        return weatherLocalData.getAllWeather()
    }


    override suspend fun insertDataFaviourit(fav: FaviouritWeather) {
       return weatherLocalData.insertWeather(fav)
    }

    override suspend fun deletWeatherFav(fav: FaviouritWeather) {
      return weatherLocalData.deleteWeather(fav)
    }


}