package com.buymyphone.app.ui.analysis

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityAnalysisBinding
import com.buymyphone.app.ui.result.ResultActivity

class AnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysisBinding
    private val handler = Handler(Looper.getMainLooper())
    private var progress = 0

    private val steps = listOf(
        "Checking device model...",
        "Checking Android version...",
        "Checking RAM information...",
        "Checking storage information...",
        "Checking display details...",
        "Checking battery status...",
        "Checking CPU information...",
        "Checking GPU information...",
        "Checking sensors...",
        "Checking camera info...",
        "Generating score...",
        "Preparing result..."
    )

    private var stepIndex = 0

    private val analysisRunnable = object : Runnable {
        override fun run() {
            if (progress > 100) {
                goToResult()
                return
            }

            binding.progressAnalysis.progress = progress
            binding.txtProgressPercent.text = "$progress%"

            if (stepIndex < steps.size) {
                val currentStep = steps[stepIndex]
                binding.txtCurrentStatus.text = currentStep
                binding.txtLiveLog.append("\n$currentStep")
                stepIndex++
            }

            progress += 10
            handler.postDelayed(this, 250)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelAnalysis.setOnClickListener {
            handler.removeCallbacks(analysisRunnable)
            finish()
        }

        startAnalysis()
    }

    private fun startAnalysis() {
        progress = 0
        stepIndex = 0
        binding.txtCurrentStatus.text = "Starting analysis..."
        binding.txtProgressPercent.text = "0%"
        binding.txtLiveLog.text = "Booting analysis engine..."
        handler.postDelayed(analysisRunnable, 300)
    }

    private fun goToResult() {
        handler.removeCallbacks(analysisRunnable)
        startActivity(Intent(this, ResultActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(analysisRunnable)
    }
}
