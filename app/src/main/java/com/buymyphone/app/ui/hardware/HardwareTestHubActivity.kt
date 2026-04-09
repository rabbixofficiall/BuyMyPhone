package com.buymyphone.app.ui.hardware

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityHardwareTestHubBinding

class HardwareTestHubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHardwareTestHubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHardwareTestHubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTouchscreenTest.setOnClickListener {
            startActivity(Intent(this, TouchscreenTestActivity::class.java))
        }

        binding.btnDeadPixelTest.setOnClickListener {
            startActivity(Intent(this, DeadPixelTestActivity::class.java))
        }

        binding.btnSpeakerTest.setOnClickListener {
            startActivity(Intent(this, SpeakerTestActivity::class.java))
        }

        binding.btnMicTest.setOnClickListener {
            startActivity(Intent(this, MicTestActivity::class.java))
        }

        binding.btnVibrationTest.setOnClickListener {
            startActivity(Intent(this, VibrationTestActivity::class.java))
        }

        binding.btnProximityTest.setOnClickListener {
            startActivity(Intent(this, ProximityTestActivity::class.java))
        }

        binding.btnFlashlightTest.setOnClickListener {
            startActivity(Intent(this, FlashlightTestActivity::class.java))
        }

        binding.btnCameraTest.setOnClickListener {
            startActivity(Intent(this, CameraTestActivity::class.java))
        }

        binding.btnFingerprintCheck.setOnClickListener {
            startActivity(Intent(this, FingerprintCheckActivity::class.java))
        }

        binding.btnBackHardwareHub.setOnClickListener {
            finish()
        }
    }
}
