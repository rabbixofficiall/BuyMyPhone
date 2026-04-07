package com.buymyphone.app.detector

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

object DisplayDetector {

    data class DisplayInfo(
        val widthPixels: Int,
        val heightPixels: Int,
        val refreshRate: Float,
        val densityDpi: Int
    )

    fun getDisplayInfo(context: Context): DisplayInfo {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = context.display ?: windowManager.defaultDisplay
            val metrics = context.resources.displayMetrics

            DisplayInfo(
                widthPixels = metrics.widthPixels,
                heightPixels = metrics.heightPixels,
                refreshRate = display?.refreshRate ?: 60f,
                densityDpi = metrics.densityDpi
            )
        } else {
            val display = windowManager.defaultDisplay
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            display.getRealMetrics(metrics)

            DisplayInfo(
                widthPixels = metrics.widthPixels,
                heightPixels = metrics.heightPixels,
                refreshRate = display.refreshRate,
                densityDpi = metrics.densityDpi
            )
        }
    }
}
