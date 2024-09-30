package com.example.weatherapp.Model

import java.text.SimpleDateFormat
import java.util.*

data class DailyWeather(
    val dayOfWeek: String,
    val date: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val weatherDescription: String,
    val icon: String
)

fun convertToDailyWeather(dailyItems: List<ListElement>): List<DailyWeather> {
    val dailyWeatherMap = mutableMapOf<String, MutableList<ListElement>>()

    // Group forecast data by day
    for (dailyItem in dailyItems) {
        val date = getDate(dailyItem.dt) // Group by date (yyyy-MM-dd)
        if (dailyWeatherMap.containsKey(date)) {
            dailyWeatherMap[date]?.add(dailyItem)
        } else {
            dailyWeatherMap[date] = mutableListOf(dailyItem)
        }
    }

    // Create daily weather summaries
    val dailyWeatherList = dailyWeatherMap.map { (date, dailyItems) ->
        // Calculate max and min temperatures for the day
        val maxTemperature = dailyItems.maxOf { it.main.temp }
        val minTemperature = dailyItems.minOf { it.main.tempMin }

        // Get weather description and icon (e.g., from the first entry or at noon)
        val weatherDescription = dailyItems.firstOrNull()?.weather?.firstOrNull()?.description ?: ""
        val icon = dailyItems.firstOrNull()?.weather?.firstOrNull()?.icon ?: ""

        // Get day of the week
        val dayOfWeek = getDayOfWeek(dailyItems.firstOrNull()?.dt ?: 0L)

        // Create the DailyWeather object
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

private fun getDayOfWeek(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
    return dayOfWeek ?: ""
}

private fun getDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = Date(timestamp * 1000)
    return dateFormat.format(date)
}
