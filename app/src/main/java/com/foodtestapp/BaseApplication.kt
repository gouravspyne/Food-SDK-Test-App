package com.foodtestapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.spyneai.foodsdk.sdk.Spyne
import io.sentry.android.core.SentryAndroid
import java.lang.RuntimeException


@SuppressLint("StaticFieldLeak")
class BaseApplication : Application() {

    private val SENTRY_DSN =
        "https://3acad6fe551a4e60a2393fcf2aeae3b6@sentry.spyne.xyz/27"

    companion object {
        private lateinit var context: Context
        private var URBAN_PIPER_API_KEY = "b2133b29-ccf3-49c8-9616-e7a31bc491a4"
        private var GOJEK_API_KEY = "b2193b25-ccf3-49c8-9616-e7a31bc481a4"
        private var SWIGGY_API_KEY = "b2193b65-bbf3-49c8-9616-d7a31bc481a4"
        private var TEST_API_KEY = "b2193b25-ccf3-59c8-8616-e7n31bc481a4"

        fun getContext(): Context {
            return context;
        }

        fun setContext(con: Context) {
            context = con
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        SentryAndroid.init(context) { options ->
            options.dsn = SENTRY_DSN
            options.tracesSampleRate = 1.0
        }

        //disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        context = this
        setContext(this)
        Spyne.init(
            this,
            SWIGGY_API_KEY,
            AppConstants.FOOD_AND_BEV_CATEGORY_ID
        )

    }

}