package com.buymyphone.app

import android.app.Application
import androidx.multidex.MultiDex

class BuyMyPhoneApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Future initialization (database, logging, etc.)
    }

    override fun attachBaseContext(base: android.content.Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
