package com.example.weatherapp.Model

import Sys
import Weather
import Wind
import com.google.gson.annotations.SerializedName


data class Forecast (
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: List<ListElement>,
    var city: City
)

data class City (
    val id: Long,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Long,
    val timezone: Long,
    val sunrise: Long,
    val sunset: Long
)

data class Coord (
    val lat: Double,
    val lon: Double
)

data class ListElement (
    val dt: Long,
    val main: MainClass,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Long,
    val pop: Double,
    val sys: Sys,
    @SerializedName("dt_txt")
    val dtTxt: String,
    val rain: Rain? = null
)

data class Clouds(
    val all: Long
)

data class MainClass (
    val temp: Double,
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Long,
    val seaLevel: Long,
    val grndLevel: Long,
    val humidity: Long,
    val tempKf: Double
)

data class Rain (
    val the3H: Double
)

data class Sys(
    val pod: Pod
)

enum class Pod {
    D,
    N
}

data class Weather (
    val id: Long,
    val main: MainEnum,
    val description: String,
    val icon: String
)

enum class MainEnum {
    Clear,
    Clouds,
    Rain
}

data class Wind(
    val speed: Double,
    val deg: Long,
    val gust: Double
)
