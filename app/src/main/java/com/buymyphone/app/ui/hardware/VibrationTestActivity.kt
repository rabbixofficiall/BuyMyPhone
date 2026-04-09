package com.buymyphone.app.ui.hardware

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityVibrationTestBinding

class VibrationTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVibrationTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVibrationTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRunVibration.setOnClickListener {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
                binding.txtVibrationStatus.text = "Vibration sent. If you felt it, vibration works."
            } else {
                binding.txtVibrationStatus.text = "No vibrator hardware found."
            }
        }

        binding.btnBackVibrationTest.setOnClickListener {
            finish()
        }
    }
}
