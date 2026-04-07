package com.buymyphone.app

import android.app.Application
import androidx.multidex.MultiDex
import com.google.android.gms.ads.MobileAds

class BuyMyPhoneApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }

    override fun attachBaseContext(base: android.content.Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
