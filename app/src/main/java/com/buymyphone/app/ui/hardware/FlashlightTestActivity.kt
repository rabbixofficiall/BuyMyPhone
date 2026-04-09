package com.buymyphone.app.ui.hardware

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityFlashlightTestBinding

class FlashlightTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashlightTestBinding
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashlightTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = try {
            cameraManager?.cameraIdList?.firstOrNull()
        } catch (e: Exception) {
            null
        }

        binding.btnFlashOn.setOnClickListener {
            try {
                if (cameraId != null) {
                    cameraManager?.setTorchMode(cameraId!!, true)
                    binding.txtFlashStatus.text = "Flashlight ON. If light is visible, flash works."
                } else {
                    binding.txtFlashStatus.text = "Flashlight hardware not found."
                }
            } catch (e: Exception) {
                binding.txtFlashStatus.text = "Unable to enable flashlight."
            }
        }

        binding.btnFlashOff.setOnClickListener {
            try {
                if (cameraId != null) {
                    cameraManager?.setTorchMode(cameraId!!, false)
                    binding.txtFlashStatus.text = "Flashlight OFF."
                }
            } catch (_: Exception) {
            }
        }

        binding.btnBackFlashTest.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (cameraId != null) {
                cameraManager?.setTorchMode(cameraId!!, false)
            }
        } catch (_: Exception) {
        }
    }
}
