package com.example.weatherapp.map.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.Constant
import com.example.weatherapp.DataBase.DatabaseClient
import com.example.weatherapp.DataBase.WeatherLocalDataImp
import com.example.weatherapp.Model.FaviouritWeather
import com.example.weatherapp.Model.LocationLatLngPojo
import com.example.weatherapp.Model.WeatherRepoImp
import com.example.weatherapp.Model.getAddress
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMapsBinding
import com.example.weatherapp.map.view.MapsFragmentArgs
import com.example.weatherapp.map.view.MapsFragmentDirections
import com.example.weatherapp.map.viewModel.MapViewModel
import com.example.weatherapp.map.viewModel.MapsFactory
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.weatherRemotImp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var locationSharedPreferences: SharedPreferences
    private lateinit var binding: FragmentMapsBinding
    private var selectedLatLng = LatLng(-34.0, 151.0)

    private val viewModel: MapViewModel by viewModels {
        MapsFactory(
            WeatherRepoImp.getInstance(
                weatherRemotImp.getInstance(RetrofitHelper.service),
                WeatherLocalDataImp.getInstance(DatabaseClient.getInstance(requireContext()).WeatherDataBase())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        locationSharedPreferences = (activity as AppCompatActivity)
            .getSharedPreferences("LocationUsedMethod", Context.MODE_PRIVATE)

        val type = MapsFragmentArgs.fromBundle(requireArguments()).type
        when (type) {
            "Home" -> setupHomeNavigation()
            "Favorite" -> setupFavoriteSave()
            "Setting" -> setupSettingsNavigation()
            Constant.ALERT_KEY -> setupAlertNavigation()
        }
    }

    private fun setupHomeNavigation() {
        binding.mapBtn.setOnClickListener {
            val action = MapsFragmentDirections.actionMapsFragmentToNavHome().apply {
                latlon = LocationLatLngPojo(
                    "Map",
                    selectedLatLng.latitude,
                    selectedLatLng.longitude
                )
                locationSharedPreferences.edit()
                    .putFloat(Constant.MAP_LAT, selectedLatLng.latitude.toFloat())
                    .putFloat(Constant.MAP_LON, selectedLatLng.longitude.toFloat())
                    .apply()
                Log.i("TAG", "LocationLatLngPojo: { ${selectedLatLng.latitude}, ${selectedLatLng.longitude} }")
            }
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    private fun setupFavoriteSave() {
        binding.mapBtn.setOnClickListener {
            Log.d("clicked", "setupFavoriteSave: ")
            lifecycleScope.launch(Dispatchers.IO) {
                val favoriteWeather = FaviouritWeather(
                    roomId = 0,
                    adress = getAddress(requireActivity(), selectedLatLng.latitude, selectedLatLng.longitude),
                    lat = selectedLatLng.latitude,
                    lon = selectedLatLng.longitude
                )
                viewModel.insertLocationFav(favoriteWeather)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Location saved to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSettingsNavigation() {
        binding.mapBtn.setOnClickListener {
            val action = MapsFragmentDirections.actionMapsFragmentToNavHome().apply {
                latlon = LocationLatLngPojo(
                    "Map",
                    selectedLatLng.latitude,
                    selectedLatLng.longitude
                )
                locationSharedPreferences.edit()
                    .putFloat(Constant.MAP_LAT, selectedLatLng.latitude.toFloat())
                    .putFloat(Constant.MAP_LON, selectedLatLng.longitude.toFloat())
                    .apply()
                Log.i("MapFragment", "Setting: LocationLatLngPojo: { ${selectedLatLng.latitude}, ${selectedLatLng.longitude} }")
            }
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    private fun setupAlertNavigation() {
        binding.mapBtn.setOnClickListener {
            val action = MapsFragmentDirections.actionMapsFragmentToNavAlert().apply {
                latlon = LocationLatLngPojo(
                    "Map",
                    selectedLatLng.latitude,
                    selectedLatLng.longitude
                )
            }
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val currentLocation = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(currentLocation).title("Your Location"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            selectedLatLng = latLng
        }

        map.setOnCameraMoveListener {
            val targetLatLng = map.cameraPosition.target
            map.clear()
            selectedLatLng = targetLatLng
            map.addMarker(MarkerOptions().position(targetLatLng).title("Camera Moved Location"))
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }
}
