package com.example.weatherapp.DataBase


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.Model.AlertPojo
import com.example.weatherapp.Model.FaviouritWeather
import kotlinx.coroutines.flow.Flow
@Dao
interface Dao {
//favourit
    @Query("SELECT * FROM favorite")
    fun getFavoriteWeather(): Flow<List<FaviouritWeather>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeather(product: FaviouritWeather)
    @Delete
    suspend fun deleteWeather(product: FaviouritWeather)

    /////Alert
    @Query("Select * from AlertTable " )
    fun getAlert():Flow<List<AlertPojo>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlert(alert:AlertPojo)
    @Delete

    suspend fun deleteAlert(alert:AlertPojo)
    @Query("select * from AlertTable where id = :id limit 1")
    fun getAlertWithId(id: String): AlertPojo
}