package com.example.weatherapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.weatherapp.Model.ListElement
import com.example.weatherapp.R


import com.example.weatherapp.databinding.HomeForcastHourlyBinding

class HomeHourlyAdapter(val context: Context): ListAdapter<ListElement, HomeHourlyAdapter.ViewHolder>(MyDiffutil()) {
    lateinit var binding: HomeForcastHourlyBinding

    class MyDiffutil: DiffUtil.ItemCallback<ListElement>() {
        override fun areItemsTheSame(oldItem: ListElement, newItem: ListElement): Boolean {
            return oldItem.dt == newItem.dt// Assuming ListElement has an 'id' field
        }

        override fun areContentsTheSame(oldItem: ListElement, newItem: ListElement): Boolean {
            return oldItem == newItem
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HomeForcastHourlyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bindData(getItem(position))
    }




    inner class ViewHolder(var binding: HomeForcastHourlyBinding): RecyclerView.ViewHolder(binding.root){
        fun bindData(hourlyWeather: ListElement) {
            binding.apply {
                timeid.text =hourlyWeather.dtTxt
                tempH.text =hourlyWeather.main.temp.toString()
                iconH.setImageResource(getIcon(hourlyWeather.weather.get(0).icon))

            }}
    }
    private fun getIcon(icon: String): Int {
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





