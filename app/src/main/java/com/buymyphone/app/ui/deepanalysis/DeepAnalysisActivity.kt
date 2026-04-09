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
import com.buymyphone.app.ui.buysell.BuySellHelperActivity
import com.buymyphone.app.ui.hardware.HardwareTestHubActivity
import java.util.Locale

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
        "Checking hardware capability and fingerprint state...",
        "Checking sensor availability...",
        "Running battery deep starter analysis...",
        "Building buy/sell helper verdict..."
    )

    private var currentStepIndex = 0
    private lateinit var analysisResult: DeepAnalysisResult
    private lateinit var softwareReportText: String
    private lateinit var hardwareReportText: String

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.values.all { it }
            if (granted) {
                startDeepAnalysisInternal()
            } else {
                binding.txtDeepStatus.text = "Required permissions were denied."
                appendLog("[WARN] Required permissions denied. Deep analysis cannot continue.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeepAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRunSoftwareTest.setOnClickListener {
            checkPermissionsAndStart()
        }

        binding.btnOpenHardwareTests.setOnClickListener {
            startActivity(Intent(this, HardwareTestHubActivity::class.java))
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
        binding.txtDeepStatus.text = "Starting software deep analysis..."
        binding.txtDeepLog.text = ""

        val info = DeviceInfoDetector.getBasicDeviceInfo(this)
        analysisResult = DeepAnalysisEngine.run(this, info)

        softwareReportText = buildSoftwareReport(info, analysisResult)
        hardwareReportText = buildHardwareReport(analysisResult)

        runNextStep()
    }

    private fun runNextStep() {
        if (currentStepIndex >= steps.size) {
            openBuySellHelper()
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
                analysisResult.usedPhoneWarnings.forEach { appendLog("[WARN] $it") }
            }
            2 -> appendLog("[INFO] ${analysisResult.displaySuspicion}")
            3 -> appendLog("[INFO] Hardware capability check:\n${analysisResult.hardwareStarterVerdict}")
            4 -> appendLog("[INFO] Sensor availability check:\n${analysisResult.sensorStarterVerdict}")
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
        }, 700)
    }

    private fun openBuySellHelper() {
        val buyReasonsText = if (analysisResult.buyReasons.isEmpty()) {
            "- No strong positive reason found."
        } else {
            analysisResult.buyReasons.joinToString(separator = "\n") { "- $it" }
        }

        val avoidReasonsText = if (analysisResult.avoidReasons.isEmpty()) {
            "- No strong negative reason found."
        } else {
            analysisResult.avoidReasons.joinToString(separator = "\n") { "- $it" }
        }

        val intent = Intent(this, BuySellHelperActivity::class.java).apply {
            putExtra("final_verdict", analysisResult.finalVerdict)
            putExtra("buy_reasons", buyReasonsText)
            putExtra("avoid_reasons", avoidReasonsText)
            putExtra("software_report", softwareReportText)
            putExtra("hardware_report", hardwareReportText)
        }
        startActivity(intent)
    }

    private fun buildSoftwareReport(
        info: com.buymyphone.app.model.BasicDeviceInfo,
        result: DeepAnalysisResult
    ): String {
        return buildString {
            appendLine("BUYMYPHONE SOFTWARE DEEP ANALYSIS")
            appendLine("================================")
            appendLine("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
            appendLine("Android: ${info.androidVersion}")
            appendLine("SDK: ${info.sdkInt}")
            appendLine("Security Patch: ${info.securityPatch}")
            appendLine()
            appendLine("Final Verdict:")
            appendLine(result.finalVerdict)
            appendLine()
            appendLine("Buy Reasons:")
            if (result.buyReasons.isEmpty()) appendLine("- None")
            result.buyReasons.forEach { appendLine("- $it") }
            appendLine()
            appendLine("Avoid Reasons:")
            if (result.avoidReasons.isEmpty()) appendLine("- None")
            result.avoidReasons.forEach { appendLine("- $it") }
            appendLine()
            appendLine("Warnings:")
            if (result.usedPhoneWarnings.isEmpty()) appendLine("- None")
            result.usedPhoneWarnings.forEach { appendLine("- $it") }
            appendLine()
            appendLine("Display Suspicion:")
            appendLine(result.displaySuspicion)
            appendLine()
            appendLine("Battery Verdict:")
            appendLine(result.batteryVerdict)
            appendLine()
            appendLine("Sensor Summary:")
            appendLine(result.sensorStarterVerdict)
            appendLine()
            appendLine("Core Device Info:")
            appendLine("RAM: ${"%.2f".format(Locale.US, info.totalRamGb)} GB")
            appendLine("Storage: ${"%.2f".format(Locale.US, info.totalStorageGb)} GB")
            appendLine("Display: ${info.displayWidth} x ${info.displayHeight}")
            appendLine("Refresh Rate: ${"%.2f".format(Locale.US, info.refreshRate.toDouble())} Hz")
            appendLine("Battery Health: ${info.batteryHealthText}")
            appendLine("Battery Temperature: ${info.batteryTemperatureCelsius} °C")
            appendLine("SoC: ${info.socModel}")
            appendLine("GPU: ${info.gpuRenderer}")
            appendLine("Rear Cameras: ${info.rearCameraCount}")
            appendLine("Front Cameras: ${info.frontCameraCount}")
            appendLine("Best Rear Camera: ${"%.2f".format(Locale.US, info.bestRearCameraMp)} MP")
            appendLine("Best Front Camera: ${"%.2f".format(Locale.US, info.bestFrontCameraMp)} MP")
            appendLine("OIS: ${if (info.hasOis) "Yes" else "No"}")
            appendLine("Autofocus: ${if (info.hasAutoFocus) "Yes" else "No"}")
            appendLine("Flash: ${if (info.hasFlash) "Yes" else "No"}")
            appendLine("4K Support: ${if (info.supports4k) "Yes" else "No"}")
            appendLine("Total Sensors: ${info.totalSensors}")
        }
    }

    private fun buildHardwareReport(result: DeepAnalysisResult): String {
        return buildString {
            appendLine("BUYMYPHONE HARDWARE MANUAL ANALYSIS")
            appendLine("================================")
            appendLine("This report is for hardware/manual verification reference.")
            appendLine()
            appendLine("Hardware Capability Summary:")
            appendLine(result.hardwareStarterVerdict)
            appendLine()
            appendLine("Manual Tests Recommended:")
            appendLine("- Touchscreen full area test")
            appendLine("- Dead pixel visual test")
            appendLine("- Speaker tone check")
            appendLine("- Mic voice record check")
            appendLine("- Vibration motor feel check")
            appendLine("- Proximity reaction check")
            appendLine("- Flashlight on/off check")
            appendLine("- Camera front/rear capture check")
            appendLine("- Fingerprint enrollment and unlock check")
            appendLine()
            appendLine("Buy / Sell Helper Note:")
            appendLine("Use this hardware report together with the software report before final buying decision.")
        }
    }

    private fun appendLog(message: String) {
        if (logBuilder.isNotEmpty()) {
            logBuilder.append("\n")
        }
        logBuilder.append(message)
        binding.txtDeepLog.text = logBuilder.toString()
    }
}
