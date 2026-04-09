package com.buymyphone.app.deep

import com.buymyphone.app.model.BasicDeviceInfo
import com.buymyphone.app.model.DeepAnalysisResult
import com.buymyphone.app.utils.RootCheckUtils

object DeepAnalysisEngine {

    fun run(info: BasicDeviceInfo): DeepAnalysisResult {
        val warnings = mutableListOf<String>()
        val buyReasons = mutableListOf<String>()
        val avoidReasons = mutableListOf<String>()

        val displaySuspicion = detectDisplaySuspicion(info)
        val batteryVerdict = buildBatteryVerdict(info)
        val hardwareStarterVerdict = buildHardwareStarterVerdict(info)
        val sensorStarterVerdict = buildSensorStarterVerdict(info)

        if (displaySuspicion != "No strong display replacement suspicion detected.") {
            warnings.add(displaySuspicion)
            avoidReasons.add("Display data pattern looks unusual and should be checked manually.")
        } else {
            buyReasons.add("Display information looks normal from software-side checks.")
        }

        if (!info.batteryHealthText.equals("Good", true)) {
            warnings.add("Battery health is not reported as good.")
            avoidReasons.add("Battery health needs attention.")
        } else {
            buyReasons.add("Battery health looks acceptable.")
        }

        if (info.batteryLevelPercent in 0..20) {
            warnings.add("Battery level is very low during inspection.")
            avoidReasons.add("Phone should be checked again after charging.")
        }

        if (!info.hasGyroscope) {
            warnings.add("Gyroscope sensor missing.")
            avoidReasons.add("Some gaming and stabilization experiences may be limited.")
        } else {
            buyReasons.add("Gyroscope is available.")
        }

        if (!info.hasProximity) {
            warnings.add("Proximity sensor missing.")
            avoidReasons.add("Call behavior and auto screen-off may be affected.")
        }

        if (!info.hasLightSensor) {
            warnings.add("Light sensor missing.")
            avoidReasons.add("Auto brightness experience may be limited.")
        }

        if (info.totalRamGb >= 8) {
            buyReasons.add("RAM capacity is good for multitasking.")
        } else if (info.totalRamGb < 4) {
            avoidReasons.add("RAM capacity is low for modern heavy usage.")
        }

        if (info.totalStorageGb >= 128) {
            buyReasons.add("Storage capacity is good for long-term daily use.")
        } else if (info.totalStorageGb < 64) {
            avoidReasons.add("Storage capacity is low for modern apps and media.")
        }

        if (info.refreshRate >= 90f) {
            buyReasons.add("High refresh rate display improves smoothness.")
        }

        if (info.bestRearCameraMp >= 48) {
            buyReasons.add("Rear camera resolution is decent on paper.")
        } else if (info.bestRearCameraMp in 1.0..15.99) {
            avoidReasons.add("Rear camera hardware looks basic.")
        }

        if (info.supports4k) {
            buyReasons.add("4K video support is available.")
        }

        if (RootCheckUtils.isRootSuspicious()) {
            warnings.add("Root / modified system suspicion detected.")
            avoidReasons.add("Phone may be rooted or unofficially modified.")
        } else {
            buyReasons.add("No strong root suspicion detected.")
        }

        val finalVerdict = buildFinalVerdict(buyReasons, avoidReasons)

        return DeepAnalysisResult(
            usedPhoneWarnings = warnings,
            buyReasons = buyReasons,
            avoidReasons = avoidReasons,
            displaySuspicion = displaySuspicion,
            batteryVerdict = batteryVerdict,
            hardwareStarterVerdict = hardwareStarterVerdict,
            sensorStarterVerdict = sensorStarterVerdict,
            finalVerdict = finalVerdict
        )
    }

    private fun detectDisplaySuspicion(info: BasicDeviceInfo): String {
        val width = info.displayWidth
        val height = info.displayHeight
        val density = info.densityDpi
        val refresh = info.refreshRate

        if (width <= 0 || height <= 0 || density <= 0) {
            return "Display information is incomplete. Manual inspection recommended."
        }

        if (width < 720 || height < 1280) {
            return "Low display resolution detected. Manual panel quality check recommended."
        }

        if (density < 220) {
            return "Display density is unusually low. Panel replacement suspicion should be checked manually."
        }

        if (refresh < 50f) {
            return "Refresh rate looks unusual. Manual display verification recommended."
        }

        return "No strong display replacement suspicion detected."
    }

    private fun buildBatteryVerdict(info: BasicDeviceInfo): String {
        return when {
            !info.batteryHealthText.equals("Good", true) ->
                "Battery health needs attention."

            info.batteryTemperatureCelsius >= 43 ->
                "Battery temperature is high. Overheating risk should be checked."

            info.batteryLevelPercent in 0..15 ->
                "Battery level is critically low during test."

            info.isCharging ->
                "Battery is charging normally during analysis."

            else -> "Battery condition looks normal from current software checks."
        }
    }

    private fun buildHardwareStarterVerdict(info: BasicDeviceInfo): String {
        val items = mutableListOf<String>()

        items.add("Touchscreen manual test pending")
        items.add("Dead pixel manual test pending")
        items.add("Speaker manual test pending")
        items.add("Mic manual test pending")
        items.add("Vibration test pending")
        items.add("Proximity test ${if (info.hasProximity) "supported" else "not supported"}")
        items.add("Flashlight test ${if (info.hasFlash) "supported" else "not supported"}")
        items.add("Fingerprint availability software check pending")

        return items.joinToString(separator = "\n")
    }

    private fun buildSensorStarterVerdict(info: BasicDeviceInfo): String {
        return buildString {
            appendLine("Accelerometer: ${yesNo(info.hasAccelerometer)}")
            appendLine("Gyroscope: ${yesNo(info.hasGyroscope)}")
            appendLine("Compass / Magnetic Field: ${yesNo(info.hasMagneticField)}")
            appendLine("Light Sensor: ${yesNo(info.hasLightSensor)}")
            appendLine("Rotation Vector: ${yesNo(info.hasRotationVector)}")
            appendLine("Total Sensors: ${info.totalSensors}")
        }.trim()
    }

    private fun buildFinalVerdict(
        buyReasons: List<String>,
        avoidReasons: List<String>
    ): String {
        return when {
            avoidReasons.size >= 4 -> "Used phone verdict: Buy with caution. Several weak points found."
            avoidReasons.isNotEmpty() -> "Used phone verdict: Mixed result. Buy only after manual tests."
            buyReasons.size >= 4 -> "Used phone verdict: Looks good from software-side checks."
            else -> "Used phone verdict: Basic result. More manual testing recommended."
        }
    }

    private fun yesNo(value: Boolean): String = if (value) "Yes" else "No"
}
