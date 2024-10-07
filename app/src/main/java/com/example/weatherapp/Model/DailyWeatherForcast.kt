package com.example.weatherapp.Model

import androidx.core.text.util.LocalePreferences.getTemperatureUnit
import com.example.weatherapp.Constant
import java.text.SimpleDateFormat
import java.util.*

data class DailyWeather(
    val dayOfWeek: String,
    val date: String,
    val maxTemperature: String,
   val minTemperature: String,
    val weatherDescription: String,
    val icon: String
)

fun convertToDailyWeather(dailyItems: List<ListElement>, units: String, language: String): List<DailyWeather> {
    val dailyWeatherMap = dailyItems.groupBy { getDate(it.dt) } // Group by date

    val dailyWeatherList = dailyWeatherMap.map { (date, dailyGroup) ->

        // Convert temperatures based on units
        val maxTemp = when (units) {
            Constant.ENUM_UNITS.metric.toString() -> dailyGroup.maxOf { it.main.tempMax - 273.15 } // Celsius
            Constant.ENUM_UNITS.imperial.toString() -> (dailyGroup.maxOf { it.main.tempMax - 273.15 }) * 9 / 5 + 32 // Fahrenheit
            Constant.ENUM_UNITS.standard.toString() -> dailyGroup.maxOf { it.main.tempMax } // Kelvin
            else -> dailyGroup.maxOf { it.main.tempMax - 273.15 } // Default to Celsius
        }

        val minTemp = when (units) {
            Constant.ENUM_UNITS.metric.toString() -> dailyGroup.minOf { it.main.tempMin - 273.15 } // Celsius
            Constant.ENUM_UNITS.imperial.toString() -> (dailyGroup.minOf { it.main.tempMin - 273.15 }) * 9 / 5 + 32 // Fahrenheit
            Constant.ENUM_UNITS.standard.toString() -> dailyGroup.minOf { it.main.tempMin } // Kelvin
            else -> dailyGroup.minOf { it.main.tempMin - 273.15 } // Default to Celsius
        }

        // Get temperature unit for the given language and unit system
//        val tempUnit = getTemperatureUnit(units, language)

        val maxTemperature = "${String.format("%.0f", maxTemp)}°"
        val minTemperature = "${String.format("%.0f", minTemp)}°"

        val weatherDescription = dailyGroup.firstOrNull()?.weather?.firstOrNull()?.description ?: ""
        val icon = dailyGroup.firstOrNull()?.weather?.firstOrNull()?.icon ?: ""

        val dayOfWeek = getDayOfWeek(dailyGroup.firstOrNull()?.dt ?: 0L, language)

        DailyWeather(
            dayOfWeek = dayOfWeek,
            date = date,
            maxTemperature = maxTemperature,
            minTemperature = minTemperature,
            weatherDescription = weatherDescription,
            icon = icon
        )
    }

    return dailyWeatherList
}

// Modify the getDayOfWeek function to properly handle Locale
private fun getDayOfWeek(timestamp: Long, language: String): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000

    // Use a Locale object instead of passing a String
    val locale = Locale(language) // Convert the language code (e.g., "en", "ar") to a Locale
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale)

    return dayOfWeek ?: ""
}

// No changes needed for getDate function
private fun getDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = Date(timestamp * 1000)
    return dateFormat.format(date)
}
