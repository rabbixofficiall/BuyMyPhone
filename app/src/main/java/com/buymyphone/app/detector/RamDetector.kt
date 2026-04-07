package com.buymyphone.app.detector

import android.app.ActivityManager
import android.content.Context

object RamDetector {

    data class RamInfo(
        val totalRamGb: Double,
        val availableRamGb: Double,
        val lowMemory: Boolean
    )

    fun getRamInfo(context: Context): RamInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRamGb = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        val availableRamGb = memoryInfo.availMem / (1024.0 * 1024.0 * 1024.0)

        return RamInfo(
            totalRamGb = totalRamGb,
            availableRamGb = availableRamGb,
            lowMemory = memoryInfo.lowMemory
        )
    }
}
