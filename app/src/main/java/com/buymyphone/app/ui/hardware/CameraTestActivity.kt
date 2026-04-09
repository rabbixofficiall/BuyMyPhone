package com.buymyphone.app.ui.hardware

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityCameraTestBinding
import com.buymyphone.app.detector.DeviceInfoDetector
import java.util.Locale

class CameraTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val info = DeviceInfoDetector.getBasicDeviceInfo(this)

        binding.txtCameraInfo.text = buildString {
            appendLine("Total Cameras: ${info.totalCameras}")
            appendLine("Rear Cameras: ${info.rearCameraCount}")
            appendLine("Front Cameras: ${info.frontCameraCount}")
            appendLine("Best Rear Camera: ${"%.2f".format(Locale.US, info.bestRearCameraMp)} MP")
            appendLine("Best Front Camera: ${"%.2f".format(Locale.US, info.bestFrontCameraMp)} MP")
            appendLine("Flash: ${if (info.hasFlash) "Yes" else "No"}")
            appendLine("Autofocus: ${if (info.hasAutoFocus) "Yes" else "No"}")
            appendLine("OIS: ${if (info.hasOis) "Yes" else "No"}")
            appendLine("Video Stabilization: ${if (info.hasVideoStabilization) "Yes" else "No"}")
            appendLine("RAW Support: ${if (info.supportsRaw) "Yes" else "No"}")
            appendLine("4K Recording: ${if (info.supports4k) "Yes" else "No"}")
            appendLine()
            appendLine("Manual Check Recommendation:")
            appendLine("- Open the stock camera app")
            appendLine("- Test rear photo")
            appendLine("- Test front photo")
            appendLine("- Test video")
            appendLine("- Test focus speed")
            appendLine("- Test flash")
        }

        binding.btnBackCameraTest.setOnClickListener {
            finish()
        }
    }
}
