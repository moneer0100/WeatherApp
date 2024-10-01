package com.example.weatherapp.network

import Welcome
import com.example.weatherapp.Model.Forecast

class weatherRemotImp private constructor(weatherApiService: ApiService):WeatherRemoteData {

    val apiService:ApiService by lazy {  RetrofitHelper.retrofitinstanceCurrent.create(ApiService::class.java)
    }
    companion object {
        private var instance: weatherRemotImp? = null
        fun getInstance(weatherApiService: ApiService): weatherRemotImp {
            return instance?: synchronized(this){
                instance ?: weatherRemotImp(weatherApiService)
                    .also { instance = it }
            }
        }}

    override suspend fun getCurrentDataResponse(
        lat: Double?,
        long: Double?,
        language: String?,
        units: String?
    ): Welcome {
        return apiService.getWeatherCurrent(lat,long, language,units)
    }

    override suspend fun getForcastResponse(
        lat: Double?,
        long: Double?,
        language: String?,
        units: String?
    ): Forecast {
        return apiService.getweatherForcast(lat,long, language,units)
    }


}