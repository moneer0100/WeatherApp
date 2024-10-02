package com.example.weatherapp.ui.Favourite.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.FavState
import com.example.weatherapp.Model.FaviouritWeather
import com.example.weatherapp.Model.WeatherRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repo: WeatherRepo): ViewModel() {

private val _favList=MutableStateFlow<FavState<FaviouritWeather>>(FavState.Loading)
   val favList=_favList.asStateFlow()

    fun getFavWeather(){
        viewModelScope.launch(Dispatchers.IO) {
        repo.getAllWeatherFavoirit()
            .catch {
        error->_favList.value=FavState.Error(error)
            Log.d("moneer", "getFavWeatherError$error: ")
        }
            .collect{
                data->_favList.value=FavState.Success(data)
                Log.d("moneer", "getFavWeatherSuccse$data: ")
            }
        }
    }
    fun deleteFavWeather(favDelete:FaviouritWeather){
        viewModelScope.launch (Dispatchers.IO){
            repo.deletWeatherFav(favDelete)
        }
    }
}