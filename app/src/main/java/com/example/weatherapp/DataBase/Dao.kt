package com.example.weatherapp.DataBase

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.Model.FaviouritWeather
import kotlinx.coroutines.flow.Flow

interface Dao {
    @Query("SELECT * FROM favorite")
    fun getFavoriteWeather(): Flow<List<FaviouritWeather>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeather(product: FaviouritWeather)
    @Delete
    suspend fun deleteWeather(product: FaviouritWeather)
}