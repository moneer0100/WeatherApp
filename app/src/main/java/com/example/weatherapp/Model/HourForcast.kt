//package com.example.weatherapp.Model
//
//import java.util.Calendar
//
//data class HourlyWeather(
//    val hour: Int,
//    val amPm: String,
//    val temperature: Double,
//    val icon: String
//) {
//    val list: Any
//        get() {
//            TODO()
//        }
//}
//
//fun convertToHourlyWeather(hourlyWeather: String): List<HourlyWeather> {
//    val hourlyWeatherList = mutableListOf<HourlyWeather>()
//    val calendar = Calendar.getInstance()
//
//    for (hourlyItem in hourlyWeather) {
//        calendar.timeInMillis = hourlyItem.dt * 1000
//        val hour = calendar.get(Calendar.HOUR)
//        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
//        val temperature = hourlyItem.main.temp
//        val icon = hourlyItem.weather.firstOrNull()?.icon ?: ""
//        hourlyWeatherList.add(HourlyWeather(hour, amPm, temperature, icon ))
//    }
//
//    return hourlyWeatherList
//}