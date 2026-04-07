package com.buymyphone.app.detector

import com.buymyphone.app.model.BasicDeviceInfo

object DeviceInfoDetector {

    fun getBasicDeviceInfo(context: android.content.Context): BasicDeviceInfo {
        val androidVersion = AndroidVersionDetector.getAndroidVersion()
        val sdkInt = AndroidVersionDetector.getSdkInt()
        val securityPatch = AndroidVersionDetector.getSecurityPatch()

        val ramInfo = RamDetector.getRamInfo(context)
        val storageInfo = StorageDetector.getStorageInfo()
        val displayInfo = DisplayDetector.getDisplayInfo(context)
        val batteryInfo = BatteryDetector.getBatteryInfo(context)
        val cpuInfo = CpuDetector.getCpuInfo()
        val gpuInfo = GpuDetector.getGpuInfo()
        val sensorInfo = SensorDetector.getSensorInfo(context)
        val cameraInfo = CameraDetector.getCameraInfo(context)

        return BasicDeviceInfo(
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            securityPatch = securityPatch,
            totalRamGb = ramInfo.totalRamGb,
            availableRamGb = ramInfo.availableRamGb,
            totalStorageGb = storageInfo.totalStorageGb,
            availableStorageGb = storageInfo.availableStorageGb,
            displayWidth = displayInfo.widthPixels,
            displayHeight = displayInfo.heightPixels,
            refreshRate = displayInfo.refreshRate,
            densityDpi = displayInfo.densityDpi,
            batteryLevelPercent = batteryInfo.batteryLevelPercent,
            batteryTemperatureCelsius = batteryInfo.batteryTemperatureCelsius,
            isCharging = batteryInfo.isCharging,
            batteryHealthText = batteryInfo.healthText,
            socModel = cpuInfo.socModel,
            socManufacturer = cpuInfo.socManufacturer,
            hardware = cpuInfo.hardware,
            board = cpuInfo.board,
            supportedAbis = cpuInfo.supportedAbis,
            coreCount = cpuInfo.coreCount,
            maxCpuFreqMHz = cpuInfo.maxCpuFreqMHz,
            gpuRenderer = gpuInfo.renderer,
            gpuVendor = gpuInfo.vendor,
            gpuVersion = gpuInfo.version,
            totalSensors = sensorInfo.totalSensors,
            hasAccelerometer = sensorInfo.accelerometer,
            hasGyroscope = sensorInfo.gyroscope,
            hasProximity = sensorInfo.proximity,
            hasLightSensor = sensorInfo.light,
            hasMagneticField = sensorInfo.magneticField,
            hasBarometer = sensorInfo.barometer,
            hasStepCounter = sensorInfo.stepCounter,
            hasRotationVector = sensorInfo.rotationVector,
            hasHeartRate = sensorInfo.heartRate,
            sensorNames = sensorInfo.sensorNames,
            totalCameras = cameraInfo.totalCameras,
            rearCameraCount = cameraInfo.rearCameraCount,
            frontCameraCount = cameraInfo.frontCameraCount,
            bestRearCameraMp = cameraInfo.bestRearCameraMp,
            bestFrontCameraMp = cameraInfo.bestFrontCameraMp,
            hasFlash = cameraInfo.hasFlash,
            hasOis = cameraInfo.hasOis,
            hasAutoFocus = cameraInfo.hasAutoFocus,
            hasVideoStabilization = cameraInfo.hasVideoStabilization,
            supportsRaw = cameraInfo.supportsRaw,
            supports4k = cameraInfo.supports4k,
            cameraSummaryLines = cameraInfo.cameraSummaryLines
        )
    }
}
