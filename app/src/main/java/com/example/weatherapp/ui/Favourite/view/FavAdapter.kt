package com.example.weatherapp.ui.Favourite.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.AlertPojo
import com.example.weatherapp.Model.DailyWeather
import com.example.weatherapp.Model.FaviouritWeather
import com.example.weatherapp.databinding.FavItemBinding


class FavAdapter(private val itemClickListner:(FaviouritWeather)->Unit
  ,private val deleteClickListner:(FaviouritWeather)->Unit):
 ListAdapter<FaviouritWeather,ViewHolder>(MyDiffUtil()) {

private lateinit var binding: FavItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
val inflater:LayoutInflater=LayoutInflater.from(parent.context)
val binding=FavItemBinding.inflate(inflater,parent,false)
return ViewHolder(binding,deleteClickListner,itemClickListner)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val fav=getItem(position)
        holder.bind(fav)
    }


}
class ViewHolder( private val binding: FavItemBinding,
     private val deleteClickListner:(FaviouritWeather)->Unit
      ,  private val itemClickListner:(FaviouritWeather)->Unit)
        :RecyclerView.ViewHolder(binding.root){
    fun bind(favoriteWeather: FaviouritWeather) {
        binding.apply {
            favLocation.text = favoriteWeather.adress
            root.setOnClickListener { itemClickListner(favoriteWeather) }
            imageView4.setOnClickListener {
                deleteClickListner(favoriteWeather)
            }

        }
    }
        }



class MyDiffUtil:DiffUtil.ItemCallback<FaviouritWeather>(){
    override fun areItemsTheSame(oldItem: FaviouritWeather, newItem: FaviouritWeather): Boolean {
   return    oldItem.roomId==newItem.roomId
    }

    override fun areContentsTheSame(oldItem: FaviouritWeather, newItem: FaviouritWeather): Boolean {
    return oldItem==newItem
    }
}
