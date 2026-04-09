package com.buymyphone.app.ui.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityProximityTestBinding

class ProximityTestActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityProximityTestBinding
    private var sensorManager: SensorManager? = null
    private var proximitySensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProximityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {
            binding.txtProximityStatus.text = "Proximity sensor not available."
        }

        binding.btnBackProximityTest.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.also {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val value = event?.values?.firstOrNull() ?: return
        binding.txtProximityStatus.text = "Proximity value: $value\nIf value changes when your hand comes near, sensor works."
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
