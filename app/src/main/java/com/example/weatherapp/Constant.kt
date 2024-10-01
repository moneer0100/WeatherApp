package com.example.weatherapp

object Constant {

        const val LANGUAGE_KEY = "language"
        const val UNITS_KEY="units"
        const val LOCATION_KEY = "LocationUsedMethod"
        const val ALERT_KEY="AlertNavType"
        const val COUNTRY_NAME="CountryName"
        const val SHARED_PREFERENCE_NAME="mySharedPreferences"
        const val LOCATION_SHARED_PREFERENCE_NAME = "LocationSharedPreferences"
        const val LOCATION_SHARED_PREFERENCE = "gettingTheLocationMethod"

        const val LAST_GPS_LAT = "gps_latitude"
        const val LAST_GPS_LON = "gps_longitude"
        const val MAP_LAT = "map_latitude"
        const val MAP_LON = "map_longitude"


        enum class Enum_lANGUAGE(){ar,en}
        enum class ENUM_UNITS(){standard,metric,imperial}
        enum class ENUM_LOCATION(){gps,map}
        enum class Enum_ALERT(){alert,notification}
    }
