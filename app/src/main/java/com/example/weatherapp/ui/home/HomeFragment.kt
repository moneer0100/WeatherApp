package com.example.weatherapp.ui.home

import Welcome
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Model.Forecast
import com.example.weatherapp.R

import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.network.ApiServiceCurrent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class HomeFragment : Fragment() {
    private lateinit var hourlyAdapter: HomeHourlyAdapter
    private lateinit var listweatherForcast: Forecast
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val MY_LOCATION_PERMISSION_ID = 5005

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

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

        hourlyAdapter = HomeHourlyAdapter(requireContext())
        binding.recyclerViewhourly.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyAdapter
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Check location permissions and start fetching data
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

                // Ensure binding is not null before accessing it
                if (isAdded && _binding != null) {
                    binding.cityId.text = "Country: $country\nAddress: $fullAddress"
                }
            } else {
                if (isAdded && _binding != null) {
                    binding.cityId.text = "Unable to fetch address"
                }
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Geocoder failed: ${e.message}")
            if (isAdded && _binding != null) {
                binding.cityId.text = "Geocoder failed"
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        val locationRequest = LocationRequest.Builder(0)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude

                        if (isAdded && _binding != null) {
                            fetchWeatherData(latitude, longitude)
                            getCityAndAddress(latitude, longitude)
                        }
                    } else {
                        Log.e("HomeFragment", "Location is null")
                    }
                }
            },
            Looper.myLooper()
        )
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            val responseCurrent = ApiServiceCurrent.RetrofitHelper.service.getWeatherCurrent(
                latitude, longitude, "d14e1678acbd48238d39b72b88398c61"
            )
            val responseForecast = ApiServiceCurrent.RetrofitHelper.service.getweatherForcast(
                latitude, longitude, "d14e1678acbd48238d39b72b88398c61"
            )

            if (responseCurrent.isSuccessful && responseForecast.isSuccessful) {
                listweatherForcast = responseForecast.body()!!

                withContext(Dispatchers.Main) {
                    if (isAdded && _binding != null) {
                        responseCurrent.body()?.dt?.let { timestamp ->
                            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .format(Date(timestamp * 1000)) // Convert to milliseconds
                            binding.dateId.text = formatDate(date)
                        }

                        binding.temperatureId.text = responseCurrent.body()?.main?.temp?.toString() ?: "N/A"
                        binding.statuId.text = responseCurrent.body()?.weather?.get(0)?.description ?: "N/A"
                        responseCurrent.body()?.weather?.get(0)
                            ?.let { getWeatherIcon(it.icon) }
                            ?.let { binding.imageView.setImageResource(it) }
                        hourlyAdapter.submitList(listweatherForcast.list)
                    }
                }
            } else {
                Log.e("HomeFragment", "Weather API response failed")
            }
        }
    }

    private fun enableLocationService() {
        val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_LOCATION_PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            } else {
                Log.e("HomeFragment", "Location permission denied")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
    private fun getWeatherIcon(icon: String): Int {
        val iconValue: Int
        when (icon) {
            "01d" -> iconValue = R.drawable.clear_sky
            "01n" -> iconValue = R.drawable.clear_sky
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
}
