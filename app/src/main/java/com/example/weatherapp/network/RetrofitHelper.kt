package com.example.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val url="https://api.openweathermap.org/data/2.5/"
    val retrofitinstanceCurrent= Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val service= retrofitinstanceCurrent.create(ApiService::class.java)
}
