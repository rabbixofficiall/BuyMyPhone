package com.buymyphone.app.model

data class BasicDeviceInfo(
    val androidVersion: String,
    val sdkInt: Int,
    val securityPatch: String,
    val totalRamGb: Double,
    val availableRamGb: Double,
    val totalStorageGb: Double,
    val availableStorageGb: Double,
    val displayWidth: Int,
    val displayHeight: Int,
    val refreshRate: Float,
    val densityDpi: Int,
    val batteryLevelPercent: Int,
    val batteryTemperatureCelsius: Float,
    val isCharging: Boolean,
    val batteryHealthText: String
)
