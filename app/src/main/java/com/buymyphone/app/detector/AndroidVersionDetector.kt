package com.buymyphone.app.detector

import android.os.Build

object AndroidVersionDetector {

    fun getAndroidVersion(): String {
        return Build.VERSION.RELEASE ?: "Unknown"
    }

    fun getSdkInt(): Int {
        return Build.VERSION.SDK_INT
    }

    fun getSecurityPatch(): String {
        return if (Build.VERSION.SECURITY_PATCH.isNullOrEmpty()) {
            "Unknown"
        } else {
            Build.VERSION.SECURITY_PATCH
        }
    }
}
