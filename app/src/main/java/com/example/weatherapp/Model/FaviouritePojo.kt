package com.example.weatherapp.Model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName="favorite")
data class FaviouritWeather(
@PrimaryKey(autoGenerate = true)
var roomId:Long=0,
    val adress:String,
    val lat:Double,
    val long: Double
)


