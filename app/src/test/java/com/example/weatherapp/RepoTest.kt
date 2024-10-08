package com.example.weatherapp

import Clouds
import com.example.weatherapp.Model.Coord
import Main
import Sys
import Weather
import Welcome
import Wind
import com.example.weatherapp.Model.City
import com.example.weatherapp.Model.FaviouritWeather

import com.example.weatherapp.Model.Forecast
import com.example.weatherapp.Model.WeatherRepoImp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test


class RepoTest {
    private lateinit var fakeRemote: FakeRemoteDataSource
    private lateinit var fakeLocal: FakeLocalDataSource
    private lateinit var repo: WeatherRepoImp


    val currentWeather = Welcome(
        501,
        Coord(10.99, 44.34),
        listOf(Weather(501, "Rain", "moderate rain", "10d")),
        "stations",
        Main(285.95, 285.74, 284.94, 287.76, 1009, 94, 1009, 942),
        8412,
        Wind(2.87, 167, 5.81),
        Clouds(100),
        1728380785,
        Sys(2, 2044440, "IT", 1728364943, 1728405860),
        7200,
        "Zocca",
        200
    )

 var weatherResponse = Forecast(

       "200",
       0,
      40,
      emptyList(),
     City(0, "", com.example.weatherapp.Model.Coord(0.0,0.0), "", 0, 0, 0, 0)

    )

    @Before
    fun setup(){
    fakeRemote= FakeRemoteDataSource(currentWeather,weatherResponse)
        fakeLocal=FakeLocalDataSource()
        repo=WeatherRepoImp.getInstance(fakeRemote,fakeLocal)

    }
    @Test
    fun getForcast_longitudeAndLatitude_weatherResponse() = runBlocking {

        val result = repo.getForcastWeather(0.0, 0.0, "metric", "en").first()


        assertNotNull(result)

        assertEquals(weatherResponse, result)
    }
    @Test
    fun getcurrent_longitudeAndLatitude_weatherResponse() = runBlocking {

        val result = repo.getCurrentWeather(0.0, 0.0, "metric", "en").first()


        assertNotNull(result)

        assertEquals(currentWeather, result)
    }

    @Test
    fun insertWeather_favoriteWeatherLocation_insertedLocation() = runBlocking {
        val favoriteWeather = FaviouritWeather(
            100,
            "cairo" ,
            10.11111,
            -100.55
        )
        repo.insertDataFaviourit(favoriteWeather)

        val allFavorites = repo.getAllWeatherFavoirit().first()

        assertThat( allFavorites.contains(favoriteWeather), `is`(true))

    }
//    @Test
//    fun deleteWeather_favoriteWeatherLocation(): Unit = runBlockingTest {
//        val favoriteWeather2 = FaviouritWeather(
//            200,
//            "cairo" ,
//            10.11111,
//            -100.55
//        )
//        repo.insertDataFaviourit(favoriteWeather2)
//        repo.deletWeatherFav(favoriteWeather2)
//
//        val allFavorites = weatherRepository.getAllWeather().first()
//        assertThat( allFavorites.contains(favoriteWeather2), `is`(false))
//
//    }



}