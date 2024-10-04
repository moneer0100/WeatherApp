package com.example.weatherapp

sealed class AlretState<out T> {
    data class Sucsess<out T>(val data:List<T>):AlretState<T>()
    data class Error(val message: Throwable) : AlretState<Nothing>()
    object Loading : AlretState<Nothing>()
}