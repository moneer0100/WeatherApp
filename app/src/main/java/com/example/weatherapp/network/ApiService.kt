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
        @Query("appid") apikey:String):Welcome

    @GET("forecast")
    suspend fun getweatherForcast(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("appid") apikey:String): Forecast





}