package com.buymyphone.app.utils

import android.os.Build
import java.io.File

object RootCheckUtils {

    fun isRootSuspicious(): Boolean {
        return hasTestKeys() || hasSuBinary() || hasSuperuserApk()
    }

    fun getRootSuspicionReason(): String {
        val reasons = mutableListOf<String>()

        if (hasTestKeys()) reasons.add("Build tags contain test-keys")
        if (hasSuBinary()) reasons.add("su binary detected in common root paths")
        if (hasSuperuserApk()) reasons.add("Superuser APK path detected")

        return if (reasons.isEmpty()) {
            "No strong root suspicion detected."
        } else {
            reasons.joinToString(separator = ", ")
        }
    }

    private fun hasTestKeys(): Boolean {
        return Build.TAGS?.contains("test-keys", true) == true
    }

    private fun hasSuBinary(): Boolean {
        val paths = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/vendor/bin/su",
            "/su/bin/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun hasSuperuserApk(): Boolean {
        val paths = listOf(
            "/system/app/Superuser.apk",
            "/system/priv-app/Superuser.apk"
        )
        return paths.any { File(it).exists() }
    }
}
