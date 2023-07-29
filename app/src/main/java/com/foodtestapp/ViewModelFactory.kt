package com.foodtestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        return when{
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel() as T
            else -> throw IllegalArgumentException("ViewModelClass not found")
        }
    }

}