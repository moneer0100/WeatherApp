package com.example.weatherapp.ui.home.view

import Welcome
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val viewModel: HomeViewModel by viewModels {
        HomeViewFactory(
            WeatherRepoImp.getInstance(weatherRemotImp.getInstance(RetrofitHelper.service))
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
                        fetchWeathercurrentData(LatLng(latitude, longitude))
                        fetchWeatherforcastData(LatLng(latitude,longitude))
                        getCityAndAddress(latitude, longitude)
                    } ?: Log.e("HomeFragment", "Location is null")
                }
            },
            Looper.getMainLooper()
        )
    }
    private fun fetchWeatherforcastData(latLng: LatLng) {
        if (isConnected(requireContext())) {
            val apiKey = "d14e1678acbd48238d39b72b88398c61" // Replace with your actual API key
            viewModel.getForcastWeatherRespons(latLng.latitude, latLng.longitude, apiKey)
            viewModel.getCurrentWeatherResponse(latLng.latitude, latLng.longitude, apiKey)

            lifecycleScope.launch {
                viewModel.weatherforcast.collectLatest { viewStateResult ->
                    when (viewStateResult) {
                        is ResponseState.Success -> {
                            displayWeatherDataforcast(viewStateResult.data)
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

    private fun fetchWeathercurrentData(latLng: LatLng) {
        if (isConnected(requireContext())) {
            val apiKey = "d14e1678acbd48238d39b72b88398c61" // Replace with your actual API key
            viewModel.getForcastWeatherRespons(latLng.latitude, latLng.longitude, apiKey)
            viewModel.getCurrentWeatherResponse(latLng.latitude, latLng.longitude, apiKey)
            binding.dateId.text = formatDate(date.format(Date()))

            lifecycleScope.launch {
                // Collect current weather data
                viewModel.weathercurrent.collectLatest { viewStateResult ->
                    when (viewStateResult) {
                        is ResponseState.Success -> {
                            displayWeatherDatacurrent(viewStateResult.data)
                        }

                        is ResponseState.Loading -> {
                            // Optionally show loading UI
                        }

                        is ResponseState.Error -> {
                            displayError(viewStateResult.message.toString())
                            loadWeatherDataFromFile()
                        }
                    }
                }}

        } else {
            loadWeatherDataFromFile()
        }
    }

    private fun displayWeatherDatacurrent(weatherResponse: Welcome) {


        binding.temperatureId.text = weatherResponse.main.temp.toString()
        binding.statuId.text = weatherResponse.weather.get(0).description
        val weatherIcon = weatherResponse.weather.get(0).icon
        binding.imageView.setImageResource(getIcon(weatherIcon))
    }

    private fun displayWeatherDataforcast(weatherResponseforcast: Forecast) {
        val currentWeather = weatherResponseforcast.list
        val convertDaulyWeather= convertToDailyWeather(currentWeather)
        val convertHourlyWeather = convertToHourlyWeather(currentWeather)
        Log.d("moneer", "displayWeatherDataforcast: ${convertHourlyWeather[0].temperature}")
        hourlyAdapter.submitList(convertHourlyWeather)
        dayilyAdapter.submitList(convertDaulyWeather)
    }

    private fun displayError(message: String) {
        Log.e("HomeFragment", "Error: $message")
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }

    private fun loadWeatherDataFromFile() {
        // Handle loading from file when network is unavailable
        Log.i("HomeFragment", "Loading weather data from file")
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
