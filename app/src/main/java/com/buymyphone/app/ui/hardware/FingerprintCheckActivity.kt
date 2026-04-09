package com.buymyphone.app.ui.hardware

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityFingerprintCheckBinding

class FingerprintCheckActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFingerprintCheckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFingerprintCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtFingerprintStatus.text = getFingerprintStatus()

        binding.btnBackFingerprintCheck.setOnClickListener {
            finish()
        }
    }

    private fun getFingerprintStatus(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fingerprintManager =
                getSystemService(Context.FINGERPRINT_SERVICE) as? FingerprintManager

            when {
                fingerprintManager == null -> "Fingerprint service not available."
                !fingerprintManager.isHardwareDetected -> "Fingerprint hardware not detected."
                !fingerprintManager.hasEnrolledFingerprints() -> "Fingerprint hardware detected, but no fingerprint is enrolled."
                else -> "Fingerprint hardware is available and at least one fingerprint is enrolled."
            }
        } else {
            "Fingerprint check requires Android 6 or newer."
        }
    }
}
