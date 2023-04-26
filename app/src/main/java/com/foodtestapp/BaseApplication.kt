package com.foodtestapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.spyneai.foodsdk.needs.AppConstants
import com.spyneai.foodsdk.sdk.Spyne
import com.spyneai.foodsdk.setLocale


@SuppressLint("StaticFieldLeak")
class BaseApplication : Application() {

    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        setLocale()

        fun setContext(con: Context){
            context = con
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        context = this
        setContext(this)

        // initialize spyne SDK

        Spyne.init(this,"b2193b65-bbf3-49c8-9616-d7a31bc481a4", AppConstants.FOOD_AND_BEV_CATEGORY_ID)

    }

}