package com.example.weatherapp.DataBase



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.weatherapp.Model.AlertPojo
import com.example.weatherapp.Model.FaviouritWeather

@Database(entities = [FaviouritWeather::class,AlertPojo::class], version = 2)
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
            ).addMigrations(migration1To2).build()
                .also { instance = it }

    }}
    val migration1To2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {

            database.execSQL("ALTER TABLE FaviouritWeather ADD COLUMN new_column_name TEXT")
        }
    }
}