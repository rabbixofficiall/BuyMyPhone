package com.buymyphone.app.model

data class BasicDeviceInfo(
    val androidVersion: String,
    val sdkInt: Int,
    val securityPatch: String,
    val totalRamGb: Double,
    val availableRamGb: Double,
    val totalStorageGb: Double,
    val availableStorageGb: Double
)
