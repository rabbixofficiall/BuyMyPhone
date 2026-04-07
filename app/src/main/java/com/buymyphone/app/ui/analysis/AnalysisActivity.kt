package com.buymyphone.app.ui.analysis

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityAnalysisBinding
import com.buymyphone.app.model.AnalysisStep
import com.buymyphone.app.ui.result.ResultActivity

class AnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysisBinding
    private val handler = Handler(Looper.getMainLooper())
    private var currentStepIndex = 0
    private val logBuilder = StringBuilder()

    private val analysisSteps = listOf(
        AnalysisStep("Device Identity", "Checking brand, model, and basic phone identity..."),
        AnalysisStep("Android System", "Checking Android version, SDK, and security patch..."),
        AnalysisStep("Memory Check", "Checking total RAM and available RAM..."),
        AnalysisStep("Storage Check", "Checking total storage and available storage..."),
        AnalysisStep("Display Check", "Checking resolution, refresh rate, and density..."),
        AnalysisStep("Battery Check", "Checking battery level, temperature, and charging state..."),
        AnalysisStep("CPU Check", "Checking SoC, cores, CPU frequency, and ABI support..."),
        AnalysisStep("GPU Check", "Checking GPU renderer, vendor, and graphics version..."),
        AnalysisStep("Sensor Check", "Checking all available sensors and motion hardware..."),
        AnalysisStep("Camera Check", "Checking camera count, MP, OIS, RAW, and 4K support..."),
        AnalysisStep("Preset Matching", "Matching chipset and GPU with built-in score database..."),
        AnalysisStep("Score Calculation", "Calculating item-wise scores and overall result..."),
        AnalysisStep("Report Building", "Preparing TXT and PDF report data...")
    )

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
        currentStepIndex = 0
        logBuilder.clear()
        binding.txtLiveLog.text = "Starting analysis...\n"
        runNextStep()
    }

    private fun runNextStep() {
        if (currentStepIndex >= analysisSteps.size) {
            finishAnalysis()
            return
        }

        val step = analysisSteps[currentStepIndex]
        val progress = (((currentStepIndex + 1) * 100f) / analysisSteps.size).toInt()

        binding.txtCurrentStatus.text = "${step.title}..."
        binding.progressAnalysis.progress = progress
        binding.txtProgressPercent.text = "$progress%"

        logBuilder.append("• ").append(step.title).append(": ").append(step.description).append("\n")
        binding.txtLiveLog.text = logBuilder.toString()

        currentStepIndex++

        handler.postDelayed({
            runNextStep()
        }, 650)
    }

    private fun finishAnalysis() {
        binding.txtCurrentStatus.text = "Analysis complete"
        binding.progressAnalysis.progress = 100
        binding.txtProgressPercent.text = "100%"
        logBuilder.append("• Final result generated successfully.\n")
        binding.txtLiveLog.text = logBuilder.toString()

        handler.postDelayed({
            startActivity(Intent(this, ResultActivity::class.java))
            finish()
        }, 900)
    }
}
