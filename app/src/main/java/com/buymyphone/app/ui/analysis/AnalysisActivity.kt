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

    private val analysisSteps = listOf(
        "Checking device model...",
        "Checking Android version...",
        "Checking RAM information...",
        "Checking storage information...",
        "Checking display resolution...",
        "Checking refresh rate...",
        "Checking CPU information...",
        "Checking GPU information...",
        "Checking battery information...",
        "Checking camera features...",
        "Checking all sensors...",
        "Checking network capabilities...",
        "Calculating item scores...",
        "Preparing final report..."
    )

    private var currentStep = 0
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelAnalysis.setOnClickListener {
            finish()
        }

        startLiveAnalysis()
    }

    private fun startLiveAnalysis() {
        runNextStep()
    }

    private fun runNextStep() {
        if (currentStep < analysisSteps.size) {
            val stepText = analysisSteps[currentStep]
            val progress = ((currentStep + 1) * 100) / analysisSteps.size

            binding.txtCurrentStatus.text = stepText
            binding.progressAnalysis.progress = progress
            binding.txtProgressPercent.text = "$progress%"

            logBuilder.append("• ").append(stepText).append("\n")
            binding.txtLiveLog.text = logBuilder.toString()

            currentStep++

            handler.postDelayed({
                runNextStep()
            }, 700)
        } else {
            binding.txtCurrentStatus.text = "Analysis complete"
            binding.progressAnalysis.progress = 100
            binding.txtProgressPercent.text = "100%"
            logBuilder.append("• Analysis finished successfully.\n")
            binding.txtLiveLog.text = logBuilder.toString()

            handler.postDelayed({
                startActivity(Intent(this, ResultActivity::class.java))
                finish()
            }, 1000)
        }
    }
}
