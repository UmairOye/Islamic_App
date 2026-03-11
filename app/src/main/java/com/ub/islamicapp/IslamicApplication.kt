package com.ub.islamicapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.ub.islamicapp.utils.IslamicEventsProvider

@HiltAndroidApp
class IslamicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        IslamicEventsProvider.init(this)
    }
}
