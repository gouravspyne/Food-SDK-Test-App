package com.foodtestapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.spyneai.foodsdk.sdk.Spyne


@SuppressLint("StaticFieldLeak")
class BaseApplication : Application() {

    companion object {
        private lateinit var context: Context

        fun getContext(): Context {
            return context;
        }

        fun setContext(con: Context){
            context = con
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        //disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        context = this
        setContext(this)
        Spyne.init(
            this,
            "b2193b25-ccf3-49c8-9616-e7a31bc481a4",
            AppConstants.FOOD_AND_BEV_CATEGORY_ID
        )

    }

}