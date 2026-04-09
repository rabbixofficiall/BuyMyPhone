package com.buymyphone.app.ui.hardware

import android.content.pm.PackageManager
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
        return try {
            val pm = packageManager

            val hasFingerprint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
            } else {
                false
            }

            val hasFaceBiometric = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                pm.hasSystemFeature(PackageManager.FEATURE_FACE)
            } else {
                false
            }

            when {
                hasFingerprint ->
                    "Fingerprint hardware feature detected."

                hasFaceBiometric ->
                    "Biometric hardware detected, but fingerprint feature was not reported."

                else ->
                    "No fingerprint / biometric hardware feature detected."
            }
        } catch (e: Exception) {
            "Fingerprint / biometric check failed safely without crash."
        }
    }
}
