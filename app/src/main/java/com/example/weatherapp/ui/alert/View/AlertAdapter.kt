package com.example.weatherapp.ui.alert.View

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.AlertPojo
import com.example.weatherapp.Model.setDate
import com.example.weatherapp.Model.setTime
import com.example.weatherapp.databinding.FragmentAlertBinding
import com.example.weatherapp.databinding.ItemAlertBinding

class AlertAdapter(private val removeClickListener: RemoveClickListener) :
    ListAdapter<AlertPojo, AlertAdapter.ViewHolder>(MyDiffitul) {

    class ViewHolder(val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alertWeatherData = getItem(position)

        holder.binding.deletalert.setOnClickListener {
            removeClickListener.onRemoveClick(alertWeatherData)
        }

        holder.binding.DateALert.setDate(alertWeatherData.end)
        holder.binding.timeid.setTime(alertWeatherData.end)
    }

    object MyDiffitul : DiffUtil.ItemCallback<AlertPojo>() {
        override fun areItemsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
            return oldItem == newItem
        }

    }

    class RemoveClickListener(val removeClickListener: (AlertPojo) -> Unit) {
        fun onRemoveClick(alertEntity: AlertPojo) = removeClickListener(alertEntity)
    }


}


