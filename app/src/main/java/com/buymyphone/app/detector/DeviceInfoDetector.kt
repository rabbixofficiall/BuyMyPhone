package com.buymyphone.app.detector

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Size
import android.view.WindowManager
import com.buymyphone.app.model.BasicDeviceInfo
import java.io.File
import java.util.Locale

object DeviceInfoDetector {

    fun getBasicDeviceInfo(context: Context): BasicDeviceInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRamGb = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        val availableRamGb = memoryInfo.availMem / (1024.0 * 1024.0 * 1024.0)

        val statFs = StatFs(Environment.getDataDirectory().absolutePath)
        val totalStorageGb = statFs.totalBytes / (1024.0 * 1024.0 * 1024.0)
        val availableStorageGb = statFs.availableBytes / (1024.0 * 1024.0 * 1024.0)

        val displayMetrics = context.resources.displayMetrics
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val densityDpi = displayMetrics.densityDpi
        val refreshRate = getRefreshRate(context)

        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val batteryLevelPercent = batteryIntent?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) ((level * 100f) / scale).toInt() else -1
        } ?: -1

        val batteryTemperatureCelsius: Float = batteryIntent?.let {
            val temp = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            if (temp >= 0) temp / 10f else -1f
        } ?: -1f

        val isCharging = batteryIntent?.let {
            val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        } ?: false

        val batteryHealthText = batteryIntent?.let {
            when (it.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
                else -> "Unknown"
            }
        } ?: "Unknown"

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        val sensorNames = allSensors.map { it.name }

        val cameraInfo = readCameraInfo(context)
        val gpuRenderer = readGpuRenderer()

        return BasicDeviceInfo(
            androidVersion = Build.VERSION.RELEASE ?: "Unknown",
            sdkInt = Build.VERSION.SDK_INT,
            securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Build.VERSION.SECURITY_PATCH ?: "Unknown"
            } else {
                "Unknown"
            },
            totalRamGb = totalRamGb,
            availableRamGb = availableRamGb,
            totalStorageGb = totalStorageGb,
            availableStorageGb = availableStorageGb,
            displayWidth = displayWidth,
            displayHeight = displayHeight,
            refreshRate = refreshRate,
            densityDpi = densityDpi,
            batteryLevelPercent = batteryLevelPercent,
            batteryTemperatureCelsius = batteryTemperatureCelsius,
            isCharging = isCharging,
            batteryHealthText = batteryHealthText,
            socModel = Build.HARDWARE ?: "Unknown",
            socManufacturer = detectSocManufacturer(Build.HARDWARE ?: ""),
            coreCount = Runtime.getRuntime().availableProcessors(),
            maxCpuFreqMHz = readMaxCpuFreqMHz(),
            supportedAbis = Build.SUPPORTED_ABIS.joinToString(),
            gpuRenderer = gpuRenderer,
            gpuVendor = "Unknown",
            gpuVersion = "Unknown",
            totalCameras = cameraInfo.totalCameras,
            rearCameraCount = cameraInfo.rearCameraCount,
            frontCameraCount = cameraInfo.frontCameraCount,
            bestRearCameraMp = cameraInfo.bestRearMp,
            bestFrontCameraMp = cameraInfo.bestFrontMp,
            hasFlash = cameraInfo.hasFlash,
            hasOis = cameraInfo.hasOis,
            hasAutoFocus = cameraInfo.hasAutoFocus,
            hasVideoStabilization = cameraInfo.hasVideoStabilization,
            supportsRaw = cameraInfo.supportsRaw,
            supports4k = cameraInfo.supports4k,
            cameraSummaryLines = cameraInfo.summaryLines,
            totalSensors = allSensors.size,
            hasAccelerometer = hasSensor(sensorManager, Sensor.TYPE_ACCELEROMETER),
            hasGyroscope = hasSensor(sensorManager, Sensor.TYPE_GYROSCOPE),
            hasProximity = hasSensor(sensorManager, Sensor.TYPE_PROXIMITY),
            hasLightSensor = hasSensor(sensorManager, Sensor.TYPE_LIGHT),
            hasMagneticField = hasSensor(sensorManager, Sensor.TYPE_MAGNETIC_FIELD),
            hasBarometer = hasSensor(sensorManager, Sensor.TYPE_PRESSURE),
            hasStepCounter = hasSensor(sensorManager, Sensor.TYPE_STEP_COUNTER),
            hasRotationVector = hasSensor(sensorManager, Sensor.TYPE_ROTATION_VECTOR),
            hasHeartRate = hasSensor(sensorManager, Sensor.TYPE_HEART_RATE),
            sensorNames = sensorNames,
            hardware = Build.HARDWARE ?: "Unknown",
            board = Build.BOARD ?: "Unknown"
        )
    }

    private fun getRefreshRate(context: Context): Float {
        return try {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display?.refreshRate ?: 60f
            } else {
                @Suppress("DEPRECATION")
                wm.defaultDisplay.refreshRate
            }
        } catch (e: Exception) {
            60f
        }
    }

    private fun hasSensor(sensorManager: SensorManager, type: Int): Boolean {
        return sensorManager.getDefaultSensor(type) != null
    }

    private fun detectSocManufacturer(hardware: String): String {
        val lower = hardware.lowercase(Locale.US)
        return when {
            lower.contains("qcom") || lower.contains("sm") -> "Qualcomm"
            lower.contains("mt") || lower.contains("mediatek") -> "MediaTek"
            lower.contains("exynos") -> "Samsung"
            lower.contains("tensor") -> "Google"
            lower.contains("ums") || lower.contains("sp") || lower.contains("unisoc") -> "Unisoc"
            else -> "Unknown"
        }
    }

    private fun readMaxCpuFreqMHz(): Int {
        return try {
            val file = File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
            if (!file.exists()) return -1
            val value = file.readText().trim().toIntOrNull() ?: return -1
            value / 1000
        } catch (e: Exception) {
            -1
        }
    }

    private fun readGpuRenderer(): String {
        return try {
            Build.HARDWARE ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private data class CameraInfoResult(
        val totalCameras: Int,
        val rearCameraCount: Int,
        val frontCameraCount: Int,
        val bestRearMp: Double,
        val bestFrontMp: Double,
        val hasFlash: Boolean,
        val hasOis: Boolean,
        val hasAutoFocus: Boolean,
        val hasVideoStabilization: Boolean,
        val supportsRaw: Boolean,
        val supports4k: Boolean,
        val summaryLines: List<String>
    )

    private fun readCameraInfo(context: Context): CameraInfoResult {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val ids = cameraManager.cameraIdList

            var rearCount = 0
            var frontCount = 0
            var bestRearMp = 0.0
            var bestFrontMp = 0.0
            var hasFlash = false
            var hasOis = false
            var hasAutoFocus = false
            var hasVideoStabilization = false
            var supportsRaw = false
            var supports4k = false
            val summary = mutableListOf<String>()

            ids.forEach { id ->
                val c = cameraManager.getCameraCharacteristics(id)
                val facing = c.get(CameraCharacteristics.LENS_FACING)
                val flash = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val afModes = c.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES) ?: intArrayOf()
                val oisModes = c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION) ?: intArrayOf()
                val caps = c.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES) ?: intArrayOf()
                val map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                val pixelSize = c.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
                val mp = if (pixelSize != null) {
                    (pixelSize.width.toDouble() * pixelSize.height.toDouble()) / 1_000_000.0
                } else {
                    0.0
                }

                val supports4kLocal = map?.getOutputSizes(android.graphics.ImageFormat.JPEG)?.any { size: Size ->
                    size.width >= 3840 || size.height >= 2160
                } == true

                val cameraLabel = when (facing) {
                    CameraCharacteristics.LENS_FACING_FRONT -> "Front"
                    CameraCharacteristics.LENS_FACING_BACK -> "Rear"
                    CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
                    else -> "Unknown"
                }

                summary.add(
                    "$cameraLabel camera — ${"%.2f".format(Locale.US, mp)} MP, " +
                        "Flash: ${if (flash) "Yes" else "No"}, " +
                        "AF: ${if (afModes.isNotEmpty() && afModes.any { it != CameraCharacteristics.CONTROL_AF_MODE_OFF }) "Yes" else "No"}, " +
                        "OIS: ${if (oisModes.isNotEmpty() && oisModes.any { it != CameraCharacteristics.LENS_OPTICAL_STABILIZATION_MODE_OFF }) "Yes" else "No"}, " +
                        "4K: ${if (supports4kLocal) "Yes" else "No"}"
                )

                when (facing) {
                    CameraCharacteristics.LENS_FACING_BACK -> {
                        rearCount++
                        if (mp > bestRearMp) bestRearMp = mp
                    }
                    CameraCharacteristics.LENS_FACING_FRONT -> {
                        frontCount++
                        if (mp > bestFrontMp) bestFrontMp = mp
                    }
                }

                if (flash) hasFlash = true
                if (afModes.isNotEmpty() && afModes.any { it != CameraCharacteristics.CONTROL_AF_MODE_OFF }) {
                    hasAutoFocus = true
                }
                if (oisModes.isNotEmpty() && oisModes.any { it != CameraCharacteristics.LENS_OPTICAL_STABILIZATION_MODE_OFF }) {
                    hasOis = true
                    hasVideoStabilization = true
                }
                if (caps.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)) {
                    supportsRaw = true
                }
                if (supports4kLocal) {
                    supports4k = true
                }
            }

            CameraInfoResult(
                totalCameras = ids.size,
                rearCameraCount = rearCount,
                frontCameraCount = frontCount,
                bestRearMp = bestRearMp,
                bestFrontMp = bestFrontMp,
                hasFlash = hasFlash,
                hasOis = hasOis,
                hasAutoFocus = hasAutoFocus,
                hasVideoStabilization = hasVideoStabilization,
                supportsRaw = supportsRaw,
                supports4k = supports4k,
                summaryLines = summary
            )
        } catch (e: Exception) {
            CameraInfoResult(
                totalCameras = 0,
                rearCameraCount = 0,
                frontCameraCount = 0,
                bestRearMp = 0.0,
                bestFrontMp = 0.0,
                hasFlash = false,
                hasOis = false,
                hasAutoFocus = false,
                hasVideoStabilization = false,
                supportsRaw = false,
                supports4k = false,
                summaryLines = listOf("Unable to read camera details")
            )
        }
    }
}
