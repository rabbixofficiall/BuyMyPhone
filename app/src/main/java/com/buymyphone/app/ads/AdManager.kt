package com.buymyphone.app.ads

import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

object AdManager {

    fun createBannerAd(context: Context, adUnitId: String): AdView {
        return AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
            loadAd(AdRequest.Builder().build())
        }
    }

    fun attachBanner(container: ViewGroup, adView: AdView) {
        container.removeAllViews()
        container.addView(adView)
    }
}
