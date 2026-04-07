package com.buymyphone.app.detector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

object SensorDetector {

    data class SensorInfo(
        val totalSensors: Int,
        val accelerometer: Boolean,
        val gyroscope: Boolean,
        val proximity: Boolean,
        val light: Boolean,
        val magneticField: Boolean,
        val barometer: Boolean,
        val stepCounter: Boolean,
        val rotationVector: Boolean,
        val heartRate: Boolean,
        val sensorNames: List<String>
    )

    fun getSensorInfo(context: Context): SensorInfo {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

        val names = sensors.map { it.name }

        return SensorInfo(
            totalSensors = sensors.size,
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null,
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null,
            proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null,
            light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null,
            magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null,
            barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null,
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null,
            rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null,
            heartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null,
            sensorNames = names
        )
    }
}
