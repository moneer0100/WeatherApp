package com.example.weatherapp.ui.alert.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.weatherapp.AlretState
import com.example.weatherapp.Model.AlertPojo
import com.example.weatherapp.Model.WeatherRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel(private val repo :WeatherRepo) : ViewModel() {

private val _alert= MutableStateFlow<AlretState<AlertPojo>>(AlretState.Loading)
val alert = _alert.asStateFlow()
fun getAlertviewmodel(){
    viewModelScope.launch (Dispatchers.IO){
        repo.getAlert()
            .catch { error->
                _alert.value=AlretState.Error(error)
            }.collect{listData->
                _alert.value=AlretState.Sucsess(listData)
            }
    }

}
    fun insertAlerViewModelt(alertPojo: AlertPojo){
        viewModelScope.launch(Dispatchers.IO){
            repo.insertAlert(alertPojo)

        }
    }
    fun deletAlertViewModel(alertPojo: AlertPojo){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteAlert(alertPojo)
        }
    }
}