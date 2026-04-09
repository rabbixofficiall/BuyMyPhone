package com.buymyphone.app.ui.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivitySensorLiveBinding
import java.util.Locale

class SensorLiveActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivitySensorLiveBinding
    private var sensorManager: SensorManager? = null

    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetic: Sensor? = null
    private var light: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magnetic = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        light = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (accelerometer == null) binding.txtAccelerometer.text = "Accelerometer: Not available"
        if (gyroscope == null) binding.txtGyroscope.text = "Gyroscope: Not available"
        if (magnetic == null) binding.txtCompass.text = "Compass / Magnetic Field: Not available"
        if (light == null) binding.txtLightSensor.text = "Light Sensor: Not available"

        binding.btnBackSensorLive.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        accelerometer?.also {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.also {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetic?.also {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        light?.also {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val e = event ?: return

        when (e.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                binding.txtAccelerometer.text =
                    "Accelerometer: x=${fmt(e.values[0])}, y=${fmt(e.values[1])}, z=${fmt(e.values[2])}"
            }

            Sensor.TYPE_GYROSCOPE -> {
                binding.txtGyroscope.text =
                    "Gyroscope: x=${fmt(e.values[0])}, y=${fmt(e.values[1])}, z=${fmt(e.values[2])}"
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                binding.txtCompass.text =
                    "Compass / Magnetic Field: x=${fmt(e.values[0])}, y=${fmt(e.values[1])}, z=${fmt(e.values[2])}"
            }

            Sensor.TYPE_LIGHT -> {
                binding.txtLightSensor.text =
                    "Light Sensor: ${fmt(e.values[0])} lx"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun fmt(value: Float): String {
        return String.format(Locale.US, "%.2f", value)
    }
}
