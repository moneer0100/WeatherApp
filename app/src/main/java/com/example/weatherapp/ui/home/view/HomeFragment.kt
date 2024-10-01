package com.example.weatherapp.ui.home.view

import Welcome
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Constant
import com.example.weatherapp.DataBase.DatabaseClient
import com.example.weatherapp.DataBase.WeatherLocalDataImp
import com.example.weatherapp.Model.*
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.network.ResponseState
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.weatherRemotImp
import com.example.weatherapp.ui.home.viewModel.HomeViewFactory
import com.example.weatherapp.ui.home.viewModel.HomeViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var dayilyAdapter: HomeDayAdapter
    private lateinit var locationManager:LocationManager
    private lateinit var hourlyAdapter: HomeHourlyAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val MY_LOCATION_PERMISSION_ID = 5005
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var langauages:String
    lateinit var unitss:String
    private lateinit var sharedPreferencesSettings: SharedPreferences
    lateinit var sharedPreferences:SharedPreferences
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val viewModel: HomeViewModel by viewModels {
        HomeViewFactory(
            WeatherRepoImp.getInstance(weatherRemotImp.getInstance(RetrofitHelper.service),
       WeatherLocalDataImp.getInstance(DatabaseClient.getInstance(requireContext()).WeatherDataBase()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initializeLocationClient()
        checkLocationSettings()
        sharedPreferences = requireContext().getSharedPreferences("LocationUsedMethod", Context.MODE_PRIVATE)
        langauages = sharedPreferences.getString(
            Constant.LANGUAGE_KEY,
            Constant.Enum_lANGUAGE.en.toString()
        ).toString()

        unitss = sharedPreferences.getString(
            Constant.UNITS_KEY,
            Constant.ENUM_UNITS.metric.toString()
        ).toString()

    }
  fun langaageAndUnits(){


  }
    private fun setupRecyclerView() {
        hourlyAdapter = HomeHourlyAdapter(requireContext())
        binding.recyclerViewhourly.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyAdapter
        }
        dayilyAdapter= HomeDayAdapter(requireContext())
        binding.recycleviewDay.apply {
            layoutManager=LinearLayoutManager(requireContext())
            adapter=dayilyAdapter
        }
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun initializeLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun checkLocationSettings() {
        if (checkLocationPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation()
            } else {
                enableLocationService()
            }
        } else {
            requestLocationPermissions()
        }
    }

    override fun onStart() {
        super.onStart()
        checkLocationSettings()
    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireContext() as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            MY_LOCATION_PERMISSION_ID
        )
    }

    @SuppressLint("SetTextI18n")
    private fun getCityAndAddress(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val country = address.countryName ?: "Unknown country"
                val fullAddress = address.getAddressLine(0) ?: "Unknown address"

                if (isAdded) {
                    binding.cityId.text = "Country: $country\nAddress: $fullAddress"
                }
            } else {
                if (isAdded) {
                    binding.cityId.text = "Unable to fetch address"
                }
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Geocoder failed: ${e.message}")
            if (isAdded) {
                binding.cityId.text = "Geocoder failed"
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // 10 seconds
            .build()
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        latitude = location.latitude
                        longitude = location.longitude

                        // Check if fragment is added before fetching weather data
                        if (isAdded) {
                            fetchWeathercurrentData(LatLng(latitude, longitude), langauages, unitss)
                            fetchWeatherforcastData(LatLng(latitude, longitude), langauages, unitss)
                            getCityAndAddress(latitude, longitude)
                        }
                    } ?: Log.e("HomeFragment", "Location is null")
                }
            },
            Looper.getMainLooper()
        )
    }
    private fun fetchWeatherforcastData(latLng: LatLng,language: String,units:String) {
        if (isConnected(requireContext())) {

            viewModel.getForcastWeatherRespons(latLng.latitude, latLng.longitude, langauages,unitss)
            viewModel.getCurrentWeatherResponse(latLng.latitude, latLng.longitude, langauages,unitss)

            lifecycleScope.launch {
                viewModel.weatherforcast.collectLatest { viewStateResult ->
                    when (viewStateResult) {
                        is ResponseState.Success -> {
                            displayWeatherDataforcast(viewStateResult.data,langauages,unitss)

                        }
                        is ResponseState.Loading -> {
                            // Optionally show loading UI
                        }
                        is ResponseState.Error -> {
                            displayError(viewStateResult.message.toString())
                            loadWeatherDataFromFileforcast()
                        }
                    }
                }
            }
        } else {
            loadWeatherDataFromFileforcast()
        }
    }

    private fun loadWeatherDataFromFileforcast() {
        val weatherData = readWeatherDataFromFileforcast()
        weatherData?.let {
            displayWeatherDataforcast(it, "", "")
        }
    }

    private fun readWeatherDataFromFileforcast(): Forecast?{
        val fileName = "weather_data_forcast.txt"
        val file = File(requireContext().filesDir, fileName)

        return try {
            if (file.exists()) {
                val jsonString = file.readText()
                val gson = Gson()
                gson.fromJson(jsonString, Forecast::class.java)
            } else {
                Log.e("HomeFragment", "Error: File does not exist")
                null
            }
        } catch (e: IOException) {
            Log.e("HomeFragment", "Error reading weather data from file: IOException", e)
            null
        }
    }

    private fun fetchWeathercurrentData(latLng: LatLng, language: String, units: String) {
        if (isConnected(requireContext())) {
            viewModel.getForcastWeatherRespons(latLng.latitude, latLng.longitude, language, units)
            viewModel.getCurrentWeatherResponse(latLng.latitude, latLng.longitude, language, units)

            // Check binding is initialized and fragment is added before accessing UI
            if (isAdded) {
                binding.dateId.text = formatDate(date.format(Date()))
            }

            lifecycleScope.launch {
                viewModel.weathercurrent.collectLatest { viewStateResult ->
                    when (viewStateResult) {
                        is ResponseState.Success -> {
                            // Check if binding is initialized
                            if (isAdded) {
                                displayWeatherDatacurrent(viewStateResult.data, language, units)
                            }
                        }
                        is ResponseState.Loading -> {
                            // Optionally show loading UI
                        }
                        is ResponseState.Error -> {
                            displayError(viewStateResult.message.toString())
                            loadWeatherDataFromFile()
                        }
                    }
                }
            }
        } else {
            loadWeatherDataFromFile()
        }
    }

    private fun displayWeatherDatacurrent(weatherResponse: Welcome, language: String, units: String) {
        val currentWeather = weatherResponse.main
        val temperatureUnit = getTemperatureUnit(units, language)

        // If units is metric, the temperature is already in Celsius
        val temperatureValue = if (units == Constant.ENUM_UNITS.metric.toString()) {
            currentWeather.temp.toString()  // Assuming temp is already in Celsius
        } else if (language == Constant.Enum_lANGUAGE.ar.toString()) {
            currentWeather.temp.toString().toArabicNumerals()
        } else {
            currentWeather.temp.toString()
        }

        binding.temperatureId.text = "$temperatureValue $temperatureUnit"
        binding.statuId.text = "${currentWeather.pressure}"

        // Set the weather icon
        val weatherIcon = weatherResponse.weather.firstOrNull()?.icon ?: "01d" // Default icon if not found
        binding.imageView.setImageResource(getIcon(weatherIcon))
    }

    private fun displayWeatherDataforcast(weatherResponseForecast: Forecast, language: String, units: String) {
        val forecastList = weatherResponseForecast.list
        val temperatureUnit = getTemperatureUnit(units, language)

        // Convert daily and hourly weather data
        val convertDailyWeather = convertToDailyWeather(forecastList)
        val convertHourlyWeather = convertToHourlyWeather(forecastList)

        // Submit the data to the adapters
        hourlyAdapter.submitList(convertHourlyWeather)
        dayilyAdapter.submitList(convertDailyWeather)
    }

    private fun getTemperatureUnit(units: String, language: String): String {
        return when (language) {
            Constant.Enum_lANGUAGE.ar.toString() -> {
                when (units) {
                    Constant.ENUM_UNITS.metric.toString() -> "°س"
                    Constant.ENUM_UNITS.imperial.toString() -> "°ف"
                    Constant.ENUM_UNITS.standard.toString() -> "ك"
                    else -> "°س"
                }
            }
            else -> {
                when (units) {
                    Constant.ENUM_UNITS.metric.toString() -> "°C"
                    Constant.ENUM_UNITS.imperial.toString() -> "°F"
                    Constant.ENUM_UNITS.standard.toString() -> "K"
                    else -> "°C"
                }
            }
        }
    }




    private fun displayError(message: String) {
        Log.e("HomeFragment", "Error: $message")
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }

    private fun loadWeatherDataFromFile() {
        val weatherData = readWeatherDataFromFile()
        weatherData?.let {
            displayWeatherDatacurrent(it, "", "")
        }

    }

    private fun readWeatherDataFromFile(): Welcome? {
        val fileName = "weather_data_current.txt"
        val file = File(requireContext().filesDir, fileName)

        return try {
            if (file.exists()) {
                val jsonString = file.readText()
                val gson = Gson()
                gson.fromJson(jsonString, Welcome::class.java)
            } else {
                Log.e("HomeFragment", "Error: File does not exist")
                null
            }
        } catch (e: IOException) {
            Log.e("HomeFragment", "Error reading weather data from file: IOException", e)
            null
        }
    }


    private fun enableLocationService() {
        val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
        Log.i("HomeFragment", "Location service is disabled, please enable it.")
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            dateString
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getIcon(icon: String): Int {
        val iconValue: Int
        when (icon) {
            "01d" -> iconValue = R.drawable.clear
            "01n" -> iconValue = R.drawable.clear
            "02d" -> iconValue = R.drawable.cloudy
            "02n" -> iconValue = R.drawable.cloudy
            "03n" -> iconValue = R.drawable.cloudy
            "03d" -> iconValue = R.drawable.cloudy
            "04d" -> iconValue = R.drawable.cloudy
            "04n" -> iconValue = R.drawable.cloudy
            "09d" -> iconValue = R.drawable.rain
            "09n" -> iconValue = R.drawable.rain
            "10d" -> iconValue = R.drawable.rain
            "10n" -> iconValue = R.drawable.rain
            "11d" -> iconValue = R.drawable.storm
            "11n" -> iconValue = R.drawable.storm
            "13d" -> iconValue = R.drawable.snow
            "13n" -> iconValue = R.drawable.snow
            "50d" -> iconValue = R.drawable.mist
            "50n" -> iconValue = R.drawable.mist
            else -> iconValue = R.drawable.header
        }
        return iconValue
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_LOCATION_PERMISSION_ID && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLocationEnabled()) {
                getFreshLocation()
            } else {
                enableLocationService()
            }
        } else {
            displayError("Location permissions are required to fetch weather data.")
        }
    }
}
