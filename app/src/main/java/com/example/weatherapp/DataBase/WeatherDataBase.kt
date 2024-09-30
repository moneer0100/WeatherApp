package com.example.weatherapp.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.Model.FaviouritWeather

@Database(entities = [FaviouritWeather::class], version = 1)
abstract class WeatherDataBase:RoomDatabase(){

}
object DatabaseClient{

    private var instance: WeatherDataBase? = null

    fun getInstance(context: Context): WeatherDataBase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                WeatherDataBase::class.java,
                "favorite_db",
            ).build()
                .also { instance = it }
        }
    }}