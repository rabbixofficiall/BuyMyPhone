package com.buymyphone.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.buymyphone.app.settings.SettingsManager
import com.google.android.gms.ads.MobileAds

class BuyMyPhoneApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SettingsManager.applyTheme(this)
        MobileAds.initialize(this)
    }

    override fun attachBaseContext(base: android.content.Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
