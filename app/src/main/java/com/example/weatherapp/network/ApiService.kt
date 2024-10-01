package com.example.weatherapp.network

import Welcome
import com.example.weatherapp.Model.Forecast

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    suspend fun getWeatherCurrent(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("lang") language: String?,
        @Query("units") units: String?,
        @Query("appid") apikey:String="d14e1678acbd48238d39b72b88398c61"
    ):Welcome

    @GET("forecast")
    suspend fun getweatherForcast(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("lang") language: String?,
        @Query("units") units: String?,
        @Query("appid") apikey:String="d14e1678acbd48238d39b72b88398c61"
    ): Forecast





}