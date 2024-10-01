package com.example.weatherapp.DataBase



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.Model.FaviouritWeather

@Database(entities = [FaviouritWeather::class], version = 1)
abstract class AppDatabase:RoomDatabase (){
abstract fun WeatherDataBase():Dao
}

object DatabaseClient{

    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "favorite_db",
            ).build()
                .also { instance = it }

    }}}