package com.buymyphone.app.detector

import android.os.Environment
import android.os.StatFs

object StorageDetector {

    data class StorageInfo(
        val totalStorageGb: Double,
        val availableStorageGb: Double
    )

    fun getStorageInfo(): StorageInfo {
        val path = Environment.getDataDirectory().path
        val stat = StatFs(path)

        val totalBytes = stat.totalBytes.toDouble()
        val availableBytes = stat.availableBytes.toDouble()

        val totalStorageGb = totalBytes / (1024.0 * 1024.0 * 1024.0)
        val availableStorageGb = availableBytes / (1024.0 * 1024.0 * 1024.0)

        return StorageInfo(
            totalStorageGb = totalStorageGb,
            availableStorageGb = availableStorageGb
        )
    }
}
