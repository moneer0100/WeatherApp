package com.example.weatherapp.Model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HourlyWeather(
    val time: String,
    val temperature: String,
    val icon: String
)

fun convertToHourlyWeather(hourlyWeather: List<ListElement>): List<HourlyWeather> {
    val hourlyWeatherList = mutableListOf<HourlyWeather>()
    val calendar = Calendar.getInstance()
    val timeFormat = SimpleDateFormat("hh:mma", Locale.getDefault()) // Format for 12-hour time with AM/PM
    val seenHours = mutableSetOf<String>() // Set to track hours already added

    for (hourlyItem in hourlyWeather) {
        // Set the calendar time from the timestamp
        calendar.timeInMillis = hourlyItem.dt * 1000 // Assuming dt is in seconds

        // Format the time as 12:01AM, 01:15PM, etc.
        val formattedTime = timeFormat.format(calendar.time)

        // Check if this hour has already been added
        if (seenHours.contains(formattedTime)) {
            continue // Skip this entry if the hour has already been processed
        }

        // Convert temperature from Kelvin to Celsius and format to 1 decimal place
        val temperatureInCelsius = hourlyItem.main.temp - 273.15
        val formattedTemperature = "${String.format("%.1f", temperatureInCelsius)}°C" // Add "°C"

        // Get the weather icon (if available)
        val icon = hourlyItem.weather.firstOrNull()?.icon ?: ""

        // Add the formatted time to the set of seen hours
        seenHours.add(formattedTime)

        // Add the hourly weather data to the list
        hourlyWeatherList.add(
            HourlyWeather(
                time = formattedTime,
                temperature = formattedTemperature,  // Use formatted string with "°C"
                icon = icon
            )
        )
    }

    return hourlyWeatherList
}

