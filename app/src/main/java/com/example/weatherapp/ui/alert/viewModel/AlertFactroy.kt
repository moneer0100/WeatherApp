package com.example.weatherapp.ui.alert.viewModel

import android.location.provider.ProviderProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Model.WeatherRepo
import com.example.weatherapp.Model.WeatherRepoImp

class AlertFactroy(private val _repo: WeatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(_repo) as T
        } else {
            throw java.lang.IllegalArgumentException("ViewModel Class not found")
        }
    }
}