package com.example.weatherapp.ui.Favourite.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.DataBase.DatabaseClient
import com.example.weatherapp.DataBase.WeatherLocalDataImp
import com.example.weatherapp.FavState
import com.example.weatherapp.Model.FaviouritWeather
import com.example.weatherapp.Model.WeatherRepoImp
import com.example.weatherapp.databinding.FragmentFavouriteBinding
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.weatherRemotImp
import com.example.weatherapp.ui.Favourite.view.FavouriteFragmentDirections
import com.example.weatherapp.ui.Favourite.viewModel.FavouriteFactory
import com.example.weatherapp.ui.Favourite.viewModel.FavouriteViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {
    private lateinit var favAdapter: FavAdapter
    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!


    private val viewModel: FavouriteViewModel by viewModels {
        FavouriteFactory(
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
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.recycleViewFav.layoutManager = LinearLayoutManager(requireContext())
        favAdapter = FavAdapter({}, {})
        binding.recycleViewFav.adapter = favAdapter

        viewModel.getFavWeather()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favList.collect { state ->
                when (state) {
                    is FavState.Loading -> {
                        Log.i("FavoriteFragment", "Loading state")
                        binding.recycleViewFav.visibility = View.GONE
                    }
                    is FavState.Success -> {
                        binding.recycleViewFav.visibility = View.VISIBLE
                        favAdapter.submitList(state.data)
                        Log.i("FavoriteFragment", "Success state: ${state.data}")

                        if (state.data.isEmpty()) {
                            binding.recycleViewFav.visibility = View.GONE
                            binding.placeholderImage.visibility = View.VISIBLE // Show placeholder when no favorites
                        } else {
                            favAdapter.submitList(state.data)
                            Log.i("FavoriteFragment", "Success state: ${state.data}")
                        }
                    }
                    is FavState.Error -> {
                        Log.i("FavoriteFragment", "Error: ${state.message}")
                    }
                }
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val typ = "Favorite"
            val action = FavouriteFragmentDirections.actionNavFavToMapsFragment().apply {
                type = typ
            }
            findNavController().navigate(action)
        }

        // Set onClick listener for item clicks
        val onItemClick: (FaviouritWeather) -> Unit = { favoriteWeather ->
            Log.i("FavoriteFragment", "Latitude: ${favoriteWeather.lat}, Longitude: ${favoriteWeather.lon}")
            val action = FavouriteFragmentDirections.actionNavFavToNavHome().apply {
                favoriteWeather.lat
                favoriteWeather.lon
                latlon = com.example.weatherapp.Model.LocationLatLngPojo("fav_location", favoriteWeather.lat, favoriteWeather.lon)
                Log.d("latlong", "onViewCreated:$latlon ")
            }

            findNavController().navigate(action)
            Log.i("FavoriteFragment", "Navigating to HomeFragment")
        }

        val onDeleteClick: (FaviouritWeather) -> Unit = { favoriteWeather ->
            viewModel.deleteFavWeather(favoriteWeather)
        }

        favAdapter = FavAdapter(onItemClick, onDeleteClick)
        binding.recycleViewFav.adapter = favAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
