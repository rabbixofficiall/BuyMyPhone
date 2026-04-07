package com.buymyphone.app.ui.analysis

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityAnalysisBinding
import com.buymyphone.app.model.AnalysisStep
import com.buymyphone.app.ui.result.ResultActivity
import com.buymyphone.app.utils.AnimationUtils

class AnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysisBinding
    private val handler = Handler(Looper.getMainLooper())
    private var currentStepIndex = 0
    private val logBuilder = StringBuilder()

    private val analysisSteps = listOf(
        AnalysisStep("DEVICE_ID", "Reading model, brand and phone identity..."),
        AnalysisStep("ANDROID_SYS", "Checking version, SDK and security patch..."),
        AnalysisStep("RAM_SCAN", "Reading total and available memory..."),
        AnalysisStep("STORAGE_SCAN", "Scanning internal storage blocks..."),
        AnalysisStep("DISPLAY_SCAN", "Checking resolution, refresh rate and density..."),
        AnalysisStep("BATTERY_SCAN", "Reading battery level, health and charging state..."),
        AnalysisStep("CPU_SCAN", "Inspecting SoC, cores, frequency and ABI..."),
        AnalysisStep("GPU_SCAN", "Reading renderer, vendor and graphics version..."),
        AnalysisStep("SENSOR_SCAN", "Enumerating motion and utility sensors..."),
        AnalysisStep("CAMERA_SCAN", "Checking lenses, OIS, RAW and 4K support..."),
        AnalysisStep("PRESET_MATCH", "Matching hardware with built-in score engine..."),
        AnalysisStep("SCORE_ENGINE", "Generating weighted item scores..."),
        AnalysisStep("REPORT_BUILD", "Compiling TXT and PDF report payload...")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AnimationUtils.fadeIn(this, binding.txtAnalysisTitle)
        AnimationUtils.slideUp(this, binding.txtLogTitle)

        binding.btnCancelAnalysis.setOnClickListener {
            finish()
        }

        startLiveAnalysis()
    }

    private fun startLiveAnalysis() {
        currentStepIndex = 0
        logBuilder.clear()
        binding.txtCurrentStatus.text = "> BOOT_SEQUENCE"
        binding.txtLiveLog.text = "[BOOT] Starting analysis engine...\n"
        runNextStep()
    }

    private fun runNextStep() {
        if (currentStepIndex >= analysisSteps.size) {
            finishAnalysis()
            return
        }

        val step = analysisSteps[currentStepIndex]
        val progress = (((currentStepIndex + 1) * 100f) / analysisSteps.size).toInt()

        binding.txtCurrentStatus.text = "> ${step.title}"
        binding.progressAnalysis.progress = progress
        binding.txtProgressPercent.text = "$progress%"

        logBuilder.append("[OK] ")
            .append(step.title)
            .append(" -> ")
            .append(step.description)
            .append("\n")

        binding.txtLiveLog.text = logBuilder.toString()

        AnimationUtils.pulse(this, binding.progressAnalysis)
        AnimationUtils.fadeIn(this, binding.txtCurrentStatus)

        currentStepIndex++

        handler.postDelayed({
            runNextStep()
        }, 650)
    }

    private fun finishAnalysis() {
        binding.txtCurrentStatus.text = "> ANALYSIS_COMPLETE"
        binding.progressAnalysis.progress = 100
        binding.txtProgressPercent.text = "100%"
        logBuilder.append("[DONE] Final result generated successfully.\n")
        binding.txtLiveLog.text = logBuilder.toString()

        handler.postDelayed({
            startActivity(Intent(this, ResultActivity::class.java))
            finish()
        }, 900)
    }
}
