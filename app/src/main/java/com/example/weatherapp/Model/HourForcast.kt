package com.example.weatherapp.Model

import com.example.weatherapp.Constant
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HourlyWeather(
    val time: String,
    val temperature: String,
    val icon: String
)

fun convertToHourlyWeather(hourlyWeather: List<ListElement>, units: String, language: String): List<HourlyWeather> {
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

        // Convert temperature based on units
        val temperature = when (units) {
            Constant.ENUM_UNITS.metric.toString() -> hourlyItem.main.temp - 273.15 // Celsius
            Constant.ENUM_UNITS.imperial.toString() -> (hourlyItem.main.temp - 273.15) * 9 / 5 + 32 // Fahrenheit
            Constant.ENUM_UNITS.standard.toString() -> hourlyItem.main.temp // Kelvin
            else -> hourlyItem.main.temp - 273.15 // Default to Celsius
        }

        // Get the temperature unit symbol for the given language
//        val tempUnit = getTemperatureUnit(units, language)
        val formattedTemperature = "${String.format("%.0f", temperature)} °" // Include unit symbol

        val icon = hourlyItem.weather.firstOrNull()?.icon ?: ""

        seenHours.add(formattedTime)

        hourlyWeatherList.add(
            HourlyWeather(
                time = formattedTime,
                temperature = formattedTemperature,  // Use formatted string with unit
                icon = icon
            )
        )
    }

    return hourlyWeatherList
}
