package com.buymyphone.app.ui.battery

import android.content.Context
import android.os.BatteryManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityBatteryDeepAnalysisBinding
import com.buymyphone.app.detector.DeviceInfoDetector
import java.util.Locale
import kotlin.math.abs

class BatteryDeepAnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBatteryDeepAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryDeepAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadBatteryAnalysis()

        binding.btnRefreshBatteryDeep.setOnClickListener {
            loadBatteryAnalysis()
        }

        binding.btnBackBatteryDeep.setOnClickListener {
            finish()
        }
    }

    private fun loadBatteryAnalysis() {
        val info = DeviceInfoDetector.getBasicDeviceInfo(this)
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val currentNowMicroAmp = try {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        } catch (e: Exception) {
            Int.MIN_VALUE
        }

        val currentNowMilliAmp = if (currentNowMicroAmp != Int.MIN_VALUE) {
            currentNowMicroAmp / 1000.0
        } else {
            null
        }

        val cycleEstimate = estimateCycleCount(
            batteryLevel = info.batteryLevelPercent,
            health = info.batteryHealthText,
            temperature = info.batteryTemperatureCelsius.toDouble()
        )

        val overheatWarning = when {
            info.batteryTemperatureCelsius >= 45 -> "Critical heat detected. Stop heavy use and cool the device."
            info.batteryTemperatureCelsius >= 42 -> "Battery is running hot. Monitor closely."
            info.batteryTemperatureCelsius >= 39 -> "Battery is slightly warm."
            else -> "No strong overheating warning."
        }

        val verdict = buildBatteryVerdict(
            level = info.batteryLevelPercent,
            health = info.batteryHealthText,
            temp = info.batteryTemperatureCelsius.toDouble(),
            currentNowMilliAmp = currentNowMilliAmp
        )

        binding.txtBatteryLevel.text =
            "Battery Level: ${if (info.batteryLevelPercent >= 0) "${info.batteryLevelPercent}%" else "Unknown"}"

        binding.txtBatteryHealth.text =
            "Battery Health: ${info.batteryHealthText}"

        binding.txtBatteryTemp.text =
            "Temperature: ${if (info.batteryTemperatureCelsius >= 0) "${fmt(info.batteryTemperatureCelsius.toDouble())} °C" else "Unknown"}"

        binding.txtBatteryCharging.text =
            "Charging: ${if (info.isCharging) "Yes" else "No"}"

        binding.txtBatteryCurrent.text =
            if (currentNowMilliAmp != null) {
                val state = if (currentNowMilliAmp < 0) "Discharging" else "Charging"
                "Current Now: ${fmt(abs(currentNowMilliAmp))} mA ($state)"
            } else {
                "Current Now: Not supported by this device"
            }

        binding.txtBatteryCycleEstimate.text =
            "Estimated Cycle Count: $cycleEstimate"

        binding.txtBatteryOverheat.text =
            "Overheating Warning: $overheatWarning"

        binding.txtBatteryVerdict.text =
            "Battery Verdict: $verdict"
    }

    private fun estimateCycleCount(
        batteryLevel: Int,
        health: String,
        temperature: Double
    ): String {
        return when {
            health.equals("Good", true) && temperature < 39 && batteryLevel >= 60 ->
                "Estimated low cycle wear"

            health.equals("Good", true) && temperature < 42 ->
                "Estimated moderate cycle wear"

            health.equals("Overheat", true) || temperature >= 45 ->
                "Estimated high wear or overheating stress"

            health.equals("Dead", true) || health.equals("Cold", true) ->
                "Battery state abnormal. Manual service check recommended"

            else -> "Estimated used battery condition"
        }
    }

    private fun buildBatteryVerdict(
        level: Int,
        health: String,
        temp: Double,
        currentNowMilliAmp: Double?
    ): String {
        return when {
            health.equals("Good", true) && temp < 39 && level >= 50 ->
                "Battery condition looks healthy for normal daily use."

            temp >= 45 ->
                "Battery is overheating. Avoid buying without deeper manual inspection."

            health.equals("Overheat", true) || health.equals("Dead", true) ->
                "Battery health looks risky. Replacement may be needed."

            currentNowMilliAmp != null && abs(currentNowMilliAmp) < 50 ->
                "Battery current reading is unusually low. Recheck charging or discharge behavior."

            else ->
                "Battery condition is mixed. Manual charging and drain test recommended."
        }
    }

    private fun fmt(value: Double): String {
        return String.format(Locale.US, "%.2f", value)
    }
}
