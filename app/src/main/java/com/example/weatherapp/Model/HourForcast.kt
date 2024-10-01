package com.example.weatherapp.Model

import com.example.weatherapp.Constant
import java.util.Calendar

data class HourlyWeather(
    val hour: Int,
    val amPm: String,
    val temperature: Double, // This will store the temperature in Celsius
    val icon: String
)

fun convertToHourlyWeather(hourlyWeather: List<ListElement>): List<HourlyWeather> {
    val hourlyWeatherList = mutableListOf<HourlyWeather>()
    val calendar = Calendar.getInstance()

    for (hourlyItem in hourlyWeather) {
        // Set the calendar time from the timestamp
        calendar.timeInMillis = hourlyItem.dt * 1000 // Assuming dt is in seconds

        // Get the hour and AM/PM designation
        val hour = calendar.get(Calendar.HOUR) // 12-hour format
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

        // Convert temperature from Kelvin to Celsius
        val temperature = hourlyItem.main.temp - 273.15 // Use main.temp for temperature conversion

        // Get the weather icon
        val icon = hourlyItem.weather.firstOrNull()?.icon ?: ""

        // Add the hourly weather data to the list
        hourlyWeatherList.add(HourlyWeather(hour, amPm, temperature, icon))
    }

    return hourlyWeatherList
}

