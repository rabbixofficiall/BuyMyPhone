package com.buymyphone.app.ui.hardware

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
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
            val biometricManager = BiometricManager.from(this)

            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS ->
                    "Biometric / fingerprint hardware is available and ready."

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                    "Biometric / fingerprint hardware detected, but no fingerprint is enrolled."

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                    "No fingerprint / biometric hardware detected."

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                    "Fingerprint / biometric hardware is currently unavailable."

                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                    "Security update required before biometric can be used."

                BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                    "Biometric state is unknown on this device."

                else ->
                    "Unable to determine biometric / fingerprint state."
            }
        } catch (e: Exception) {
            "Biometric / fingerprint check failed safely without crash."
        }
    }
}
