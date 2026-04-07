package com.buymyphone.app.detector

import android.os.Build
import java.io.File

object CpuDetector {

    data class CpuInfo(
        val socModel: String,
        val socManufacturer: String,
        val hardware: String,
        val board: String,
        val supportedAbis: String,
        val coreCount: Int,
        val maxCpuFreqMHz: Int
    )

    fun getCpuInfo(): CpuInfo {
        val socModel = getSocModelCompat()
        val socManufacturer = getSocManufacturerCompat()
        val hardware = Build.HARDWARE ?: "Unknown"
        val board = Build.BOARD ?: "Unknown"
        val supportedAbis = Build.SUPPORTED_ABIS?.joinToString(", ") ?: "Unknown"
        val coreCount = Runtime.getRuntime().availableProcessors()
        val maxCpuFreqMHz = readMaxCpuFreqMHz()

        return CpuInfo(
            socModel = socModel,
            socManufacturer = socManufacturer,
            hardware = hardware,
            board = board,
            supportedAbis = supportedAbis,
            coreCount = coreCount,
            maxCpuFreqMHz = maxCpuFreqMHz
        )
    }

    private fun getSocModelCompat(): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Build.SOC_MODEL ?: "Unknown"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getSocManufacturerCompat(): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Build.SOC_MANUFACTURER ?: "Unknown"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun readMaxCpuFreqMHz(): Int {
        return try {
            val cpuDir = File("/sys/devices/system/cpu/")
            val cpuFolders = cpuDir.listFiles()?.filter { it.name.matches(Regex("cpu[0-9]+")) }.orEmpty()

            val freqList = cpuFolders.mapNotNull { folder ->
                val freqFile = File(folder, "cpufreq/cpuinfo_max_freq")
                if (freqFile.exists()) {
                    freqFile.readText().trim().toIntOrNull()?.div(1000)
                } else {
                    null
                }
            }

            freqList.maxOrNull() ?: 0
        } catch (e: Exception) {
            0
        }
    }
}
