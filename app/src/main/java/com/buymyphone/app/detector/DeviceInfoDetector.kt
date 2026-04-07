package com.buymyphone.app.detector

import com.buymyphone.app.model.BasicDeviceInfo

object DeviceInfoDetector {

    fun getBasicDeviceInfo(context: android.content.Context): BasicDeviceInfo {
        val androidVersion = AndroidVersionDetector.getAndroidVersion()
        val sdkInt = AndroidVersionDetector.getSdkInt()
        val securityPatch = AndroidVersionDetector.getSecurityPatch()

        val ramInfo = RamDetector.getRamInfo(context)
        val storageInfo = StorageDetector.getStorageInfo()

        return BasicDeviceInfo(
            androidVersion = androidVersion,
            sdkInt = sdkInt,
            securityPatch = securityPatch,
            totalRamGb = ramInfo.totalRamGb,
            availableRamGb = ramInfo.availableRamGb,
            totalStorageGb = storageInfo.totalStorageGb,
            availableStorageGb = storageInfo.availableStorageGb
        )
    }
}
