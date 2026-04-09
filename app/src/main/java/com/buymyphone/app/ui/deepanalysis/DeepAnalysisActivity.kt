package com.buymyphone.app.ui.deepanalysis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.buymyphone.app.databinding.ActivityDeepAnalysisBinding
import com.buymyphone.app.deep.DeepAnalysisEngine
import com.buymyphone.app.detector.DeviceInfoDetector
import com.buymyphone.app.model.DeepAnalysisResult
import com.buymyphone.app.ui.battery.BatteryDeepAnalysisActivity
import com.buymyphone.app.ui.hardware.HardwareTestHubActivity
import com.buymyphone.app.ui.sensor.SensorLiveActivity

class DeepAnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeepAnalysisBinding
    private val handler = Handler(Looper.getMainLooper())
    private val logBuilder = StringBuilder()

    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    private val steps = listOf(
        "Checking permissions...",
        "Running used phone software checks...",
        "Checking display replacement suspicion...",
        "Preparing hardware test tools...",
        "Preparing sensor live test starter...",
        "Running battery deep starter analysis...",
        "Building buy/sell helper verdict..."
    )

    private var currentStepIndex = 0
    private lateinit var analysisResult: DeepAnalysisResult

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.values.all { it }
            if (granted) {
                startDeepAnalysisInternal()
            } else {
                binding.txtDeepStatus.text = "Required permissions were denied."
                appendLog("[WARN] Required permissions denied. Deep analysis cannot continue.")
                binding.txtDeepResult.text = "Please allow Camera and Microphone permissions to continue deep analysis."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeepAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRunDeepAnalysis.setOnClickListener {
            checkPermissionsAndStart()
        }

        binding.btnOpenHardwareTests.setOnClickListener {
            startActivity(Intent(this, HardwareTestHubActivity::class.java))
        }

        binding.btnOpenSensorLive.setOnClickListener {
            startActivity(Intent(this, SensorLiveActivity::class.java))
        }

        binding.btnOpenBatteryDeep.setOnClickListener {
            startActivity(Intent(this, BatteryDeepAnalysisActivity::class.java))
        }

        binding.btnBackDeepAnalysis.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            finish()
        }
    }

    private fun checkPermissionsAndStart() {
        val missing = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isEmpty()) {
            startDeepAnalysisInternal()
        } else {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }

    private fun startDeepAnalysisInternal() {
        currentStepIndex = 0
        logBuilder.clear()

        binding.progressDeepAnalysis.progress = 0
        binding.txtDeepProgress.text = "0%"
        binding.txtDeepStatus.text = "Starting deep analysis..."
        binding.txtDeepResult.text = "Please wait. Running software-side checks first."
        binding.txtDeepLog.text = ""

        val info = DeviceInfoDetector.getBasicDeviceInfo(this)
        analysisResult = DeepAnalysisEngine.run(info)

        runNextStep()
    }

    private fun runNextStep() {
        if (currentStepIndex >= steps.size) {
            finishDeepAnalysis()
            return
        }

        val step = steps[currentStepIndex]
        val progress = (((currentStepIndex + 1) * 100f) / steps.size).toInt()

        binding.txtDeepStatus.text = step
        binding.progressDeepAnalysis.progress = progress
        binding.txtDeepProgress.text = "$progress%"

        when (currentStepIndex) {
            0 -> appendLog("[OK] Permissions ready.")
            1 -> {
                appendLog("[OK] Used phone check complete.")
                analysisResult.usedPhoneWarnings.forEach {
                    appendLog("[WARN] $it")
                }
            }
            2 -> appendLog("[INFO] ${analysisResult.displaySuspicion}")
            3 -> appendLog("[INFO] Hardware starter:\n${analysisResult.hardwareStarterVerdict}")
            4 -> appendLog("[INFO] Sensor starter:\n${analysisResult.sensorStarterVerdict}")
            5 -> appendLog("[INFO] ${analysisResult.batteryVerdict}")
            6 -> {
                appendLog("[BUY] Reasons to buy:")
                analysisResult.buyReasons.forEach { appendLog(" + $it") }
                appendLog("[AVOID] Reasons to avoid:")
                analysisResult.avoidReasons.forEach { appendLog(" - $it") }
            }
        }

        currentStepIndex++

        handler.postDelayed({
            runNextStep()
        }, 600)
    }

    private fun finishDeepAnalysis() {
        binding.txtDeepStatus.text = "Deep analysis completed."

        val resultText = buildString {
            appendLine(analysisResult.finalVerdict)
            appendLine()
            appendLine("Why you may buy:")
            if (analysisResult.buyReasons.isEmpty()) {
                appendLine("- No strong positive reason found.")
            } else {
                analysisResult.buyReasons.forEach { appendLine("- $it") }
            }
            appendLine()
            appendLine("Why you may avoid:")
            if (analysisResult.avoidReasons.isEmpty()) {
                appendLine("- No strong negative reason found.")
            } else {
                analysisResult.avoidReasons.forEach { appendLine("- $it") }
            }
            appendLine()
            appendLine("Battery:")
            appendLine(analysisResult.batteryVerdict)
            appendLine()
            appendLine("Display suspicion:")
            appendLine(analysisResult.displaySuspicion)
        }

        binding.txtDeepResult.text = resultText
        appendLog("[DONE] Deep analysis completed.")
    }

    private fun appendLog(message: String) {
        if (logBuilder.isNotEmpty()) {
            logBuilder.append("\n")
        }
        logBuilder.append(message)
        binding.txtDeepLog.text = logBuilder.toString()
    }
}
