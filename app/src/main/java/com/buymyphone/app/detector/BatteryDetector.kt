package com.buymyphone.app.detector

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

object BatteryDetector {

    data class BatteryInfo(
        val batteryLevelPercent: Int,
        val batteryTemperatureCelsius: Float,
        val isCharging: Boolean,
        val healthText: String
    )

    fun getBatteryInfo(context: Context): BatteryInfo {
        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val temperature = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val health = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1

        val percent = if (level >= 0 && scale > 0) {
            ((level * 100.0) / scale).toInt()
        } else {
            -1
        }

        val tempC = if (temperature >= 0) {
            temperature / 10f
        } else {
            -1f
        }

        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        return BatteryInfo(
            batteryLevelPercent = percent,
            batteryTemperatureCelsius = tempC,
            isCharging = charging,
            healthText = mapHealth(health)
        )
    }

    private fun mapHealth(health: Int): String {
        return when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Unknown"
        }
    }
}
