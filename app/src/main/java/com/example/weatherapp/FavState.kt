package com.example.weatherapp

sealed class FavState <out T>{
    data class Success<out T>(val data: List<T>) : FavState<T>()
    data class Error(val message: Throwable) : FavState<Nothing>()
    object Loading : FavState<Nothing>()
}