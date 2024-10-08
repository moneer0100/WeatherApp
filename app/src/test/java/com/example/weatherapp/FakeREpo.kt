package com.example.weatherapp

import Welcome
import com.example.weatherapp.Model.AlertPojo
import com.example.weatherapp.Model.FaviouritWeather
import com.example.weatherapp.Model.Forecast
import com.example.weatherapp.Model.WeatherRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeREpo(
private var fakeRemoteDataSource: FakeRemoteDataSource,
    private var fakeLocalDataSource: FakeLocalDataSource
):WeatherRepo
{
    override suspend fun getCurrentWeather(
        lat: Double?,
        long: Double?,
        language: String,
        units: String?
    ): Flow<Welcome> {
      return flowOf(fakeRemoteDataSource.getCurrentDataResponse(lat,long,language,units))
    }

    override suspend fun getForcastWeather(
        lat: Double?,
        long: Double?,
        language: String,
        units: String?
    ): Flow<Forecast> {
       return flowOf(fakeRemoteDataSource.getForcastResponse(lat,long,language,units))
    }

    override fun getAllWeatherFavoirit(): Flow<List<FaviouritWeather>> {
       return fakeLocalDataSource.getAllWeather()
    }

    override suspend fun insertDataFaviourit(fav: FaviouritWeather) {
       fakeLocalDataSource.insertWeather(fav)
    }

    override suspend fun deletWeatherFav(fav: FaviouritWeather) {
      fakeLocalDataSource.deleteWeather(fav)
    }

    override fun getAlert(): Flow<List<AlertPojo>> {
       return fakeLocalDataSource.getAlert()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
   fakeLocalDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
       fakeLocalDataSource.deleteAlert(alert)
    }

    override fun getAlertWithId(id: String): AlertPojo {
        return fakeLocalDataSource.getAlertWithId(id)
    }
}