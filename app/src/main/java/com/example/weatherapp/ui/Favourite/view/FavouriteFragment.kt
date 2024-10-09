package com.example.weatherapp.ui.Favourite.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.DataBase.DatabaseClient
import com.example.weatherapp.DataBase.WeatherLocalDataImp
import com.example.weatherapp.Model.FaviouritWeather
import com.example.weatherapp.Model.WeatherRepoImp
import com.example.weatherapp.databinding.FragmentFavouriteBinding
import com.example.weatherapp.network.ResponseState
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.weatherRemotImp
import com.example.weatherapp.ui.Favourite.view.FavouriteFragmentDirections
import com.example.weatherapp.ui.Favourite.viewModel.FavouriteFactory
import com.example.weatherapp.ui.Favourite.viewModel.FavouriteViewModel
import kotlinx.coroutines.Dispatchers
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
                WeatherLocalDataImp.getInstance(
                    DatabaseClient.getInstance(requireContext()).WeatherDataBase()
                )
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

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.favList.collect { state ->
                when (state) {
                    is ResponseState.Loading -> {
                        Log.i("FavoriteFragment", "Loading state")
                        binding.recycleViewFav.visibility = View.GONE
                    }

                    is ResponseState.Success -> {
                        binding.recycleViewFav.visibility = View.VISIBLE
                        favAdapter.submitList(state.data)
                        Log.i("FavoriteFragment", "Success state: ${state.data}")

                        if (state.data.isEmpty()) {
                            binding.recycleViewFav.visibility = View.GONE
                            binding.placeholderImage.visibility =
                                View.VISIBLE // Show placeholder when no favorites
                        }
                    }

                    is ResponseState.Error -> {
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
            // Create an AlertDialog for item details
            AlertDialog.Builder(requireContext())
                .setTitle("Favorite Weather")
                .setMessage("Do yoy need show this location in the home")
                .setPositiveButton("View Details") { dialog, _ ->
                    val action = FavouriteFragmentDirections.actionNavFavToNavHome().apply {
                        latlon = com.example.weatherapp.Model.LocationLatLngPojo(
                            "fav_location",
                            favoriteWeather.lat,
                            favoriteWeather.lon
                        )
                    }
                    findNavController().navigate(action)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        val onDeleteClick: (FaviouritWeather) -> Unit = { favoriteWeather ->
            // Create an AlertDialog for delete confirmation
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Favorite")
                .setMessage("Are you sure you want to delete this favorite weather item?")
                .setPositiveButton("Yes") { dialog, _ ->
                    viewModel.deleteFavWeather(favoriteWeather)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        favAdapter = FavAdapter(onItemClick, onDeleteClick)
        binding.recycleViewFav.adapter = favAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
