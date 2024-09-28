package com.example.weatherapp.network

import Welcome
import com.example.weatherapp.Model.Forecast

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceCurrent {
    @GET("weather")
    suspend fun getWeatherCurrent(
        @Query("lat") lat:Double,
        @Query("lon")lon:Double,
        @Query("appid")apikey:String):Response<Welcome>

    @GET("forecast")suspend fun getweatherForcast(
        @Query("lat") lat:Double,
        @Query("lon")lon:Double,
        @Query("appid")apikey:String): Response<Forecast>
    object RetrofitHelper {

        private const val url="https://api.openweathermap.org/data/2.5/"
        val retrofitinstanceCurrent= Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service= retrofitinstanceCurrent.create(ApiServiceCurrent::class.java)

    }

   }