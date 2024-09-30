package com.example.weatherapp.ui.home.viewModel

import Welcome
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.Forecast
import com.example.weatherapp.Model.WeatherRepo
import com.example.weatherapp.network.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: WeatherRepo) : ViewModel() {
    //StateFlow is a state holder that is hot (always active)
    // and emits the latest value to any collector
    // (similar to RxJava's BehaviorSubject).

    // خنا بنستخدم seald class عشان نعرف حاله الاتصال
    // indicating that when the flow is first created, it's in a loading state
    // (commonly used for network operations).
    private val weatherCurrentLoad = MutableStateFlow<ResponseState<Welcome>>(ResponseState.Loading)
    val weathercurrent = weatherCurrentLoad.asStateFlow()

    private val currentWeatherLive = MutableLiveData<Welcome>()

    private val weatherForcastLoad = MutableStateFlow<ResponseState<Forecast>>(ResponseState.Loading)
    val weatherforcast = weatherForcastLoad.asStateFlow()

    private val forcasttWeatherLive = MutableLiveData<Forecast>()


    fun getCurrentWeatherResponse(lat:Double,long: Double,apki:String){
        viewModelScope.launch (Dispatchers.IO){
            repo.getCurrentWeather(lat,long,apki)
                ?.catch { error-> weatherCurrentLoad.value=ResponseState.Error(error)}
                ?.collect{data->weatherCurrentLoad.value=ResponseState.Success(data)
                    Log.d("moneer", "DataCurrentRecived: $data")}
        }
    }
    fun getForcastWeatherRespons(lat:Double,long: Double,apki:String){

        viewModelScope.launch(Dispatchers.IO){
            repo.getForcastWeather(lat,long,apki)
                ?.catch { error->weatherForcastLoad.value=ResponseState.Error(error)  }
                ?.collect{data->weatherForcastLoad.value=ResponseState.Success(data)
                    Log.d("moneer", "getForcastWeatherRespons:$data ")
                }
        }
    }

}