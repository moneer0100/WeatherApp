package com.example.weatherapp.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Model.WeatherRepo
import com.example.weatherapp.Model.WeatherRepoImp

class MapsFactory(private val repo: WeatherRepoImp) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(repo) as T
        } else {
            throw java.lang.IllegalArgumentException("ViewModel Class not found")
        }
}}