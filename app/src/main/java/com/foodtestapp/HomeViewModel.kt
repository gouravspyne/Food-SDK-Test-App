package com.foodtestapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {


    val startShoot: MutableLiveData<Boolean> = MutableLiveData()
    var onStartShoot:  Boolean = true
    var skuName = ""

}