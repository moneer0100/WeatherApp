package com.example.weatherapp.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.DailyWeather
import com.example.weatherapp.R
import com.example.weatherapp.databinding.HomeForcastDayBinding

class HomeDayAdapter(requireContext: Context) :ListAdapter<DailyWeather, HomeDayAdapter.ViewHolder>(
    MyDiffUtil()
) {
    lateinit var binding:HomeForcastDayBinding
    class MyDiffUtil:DiffUtil.ItemCallback<DailyWeather>() {
        override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
       return  oldItem.date==newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
       return  oldItem==newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HomeForcastDayBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val forcastDay:DailyWeather=getItem(position)
     holder.binding.dateDay.text=forcastDay.dayOfWeek
        holder.binding.degreeDay.text=forcastDay.maxTemperature.toString()
        holder.binding.degreepm.text=forcastDay.minTemperature.toString()
        holder.binding.textView.text=forcastDay.date
        holder.binding.description.text=forcastDay.weatherDescription
        holder.binding.imageView2.setImageResource(getIcon(forcastDay.icon))


    }
    class ViewHolder(var binding:HomeForcastDayBinding):RecyclerView.ViewHolder(binding.root)

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

