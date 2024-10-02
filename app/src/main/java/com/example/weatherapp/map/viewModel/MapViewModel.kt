package com.example.weatherapp.map.viewModel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.Model.FaviouritWeather
import com.example.weatherapp.Model.WeatherRepo
import com.example.weatherapp.Model.WeatherRepoImp
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel(private val repo: WeatherRepoImp):ViewModel() {
    private val _selectlocation= MutableStateFlow<LatLng?>(null)
    val selsctlocation:StateFlow<LatLng?>  = _selectlocation

    fun settLocation(latLng: LatLng){
        _selectlocation.value=latLng
    }suspend fun insertLocationFav(favWeather:FaviouritWeather){
        repo.insertDataFaviourit(favWeather)
    }


}