package com.example.weatherapp.Model

import java.text.SimpleDateFormat
import java.util.*

data class DailyWeather(
    val dayOfWeek: String,
    val date: String,
    val maxTemperature: String,
//    val minTemperature: Double,
    val weatherDescription: String,
    val icon: String
)

fun convertToDailyWeather(dailyItems: List<ListElement>): List<DailyWeather> {
    val dailyWeatherMap = dailyItems.groupBy { getDate(it.dt) } // Group by date

    val dailyWeatherList = dailyWeatherMap.map { (date, dailyGroup) ->
        // Convert and round max temperature to 1 decimal place and add "°C"
        val maxTemperature = "${String.format("%.1f", dailyGroup.maxOf { it.main.temp - 273.15 })}°C"

        // Convert and round min temperature to 1 decimal place and add "°C" (if needed)
//        val minTemperature = "${String.format("%.1f", dailyGroup.minOf { it.main.tempMin - 273.15 })}°C"

        val weatherDescription = dailyGroup.firstOrNull()?.weather?.firstOrNull()?.description ?: ""
        val icon = dailyGroup.firstOrNull()?.weather?.firstOrNull()?.icon ?: ""

        val dayOfWeek = getDayOfWeek(dailyGroup.firstOrNull()?.dt ?: 0L)

        DailyWeather(
            dayOfWeek = dayOfWeek,
            date = date,
            maxTemperature = maxTemperature,
//            minTemperature = minTemperature, // Uncomment if needed
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
