package com.buymyphone.app.ui.hardware

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.buymyphone.app.databinding.ActivityMicTestBinding
import java.io.File

class MicTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMicTestBinding
    private var recorder: MediaRecorder? = null
    private lateinit var outputFile: String

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startRecording()
            } else {
                binding.txtMicStatus.text = "Microphone permission denied."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMicTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        outputFile = File(cacheDir, "mic_test_record.3gp").absolutePath

        binding.btnStartMicTest.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
            ) {
                startRecording()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        binding.btnBackMicTest.setOnClickListener {
            finish()
        }
    }

    private fun startRecording() {
        try {
            recorder?.release()
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile)
                prepare()
                start()
            }

            binding.txtMicStatus.text = "Recording... speak into the mic."

            Handler(Looper.getMainLooper()).postDelayed({
                stopRecording()
            }, 3000)
        } catch (e: Exception) {
            binding.txtMicStatus.text = "Mic test failed to start."
        }
    }

    private fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            binding.txtMicStatus.text = "Recording completed. If no error occurred, mic likely works."
        } catch (e: Exception) {
            binding.txtMicStatus.text = "Mic recording stopped with error."
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            recorder?.release()
        } catch (_: Exception) {
        }
        recorder = null
    }
}
