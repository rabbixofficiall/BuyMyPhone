package com.buymyphone.app.ui.deepanalysis

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.buymyphone.app.databinding.ActivityDeepAnalysisBinding
import com.buymyphone.app.deep.DeepAnalysisEngine
import com.buymyphone.app.detector.DeviceInfoDetector
import com.buymyphone.app.export.PdfReportExporter
import com.buymyphone.app.model.DeepAnalysisResult
import java.text.SimpleDateFormat
import java.util.Date
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
        "Checking hardware capability and biometric state...",
        "Checking sensor availability...",
        "Running battery deep starter analysis...",
        "Building buy/sell helper verdict..."
    )

    private var currentStepIndex = 0
    private lateinit var analysisResult: DeepAnalysisResult
    private lateinit var deepReportText: String

    private val createPdfDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
            if (uri == null) {
                Toast.makeText(this, "PDF save cancelled", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val success = PdfReportExporter.export(
                context = this,
                uri = uri,
                title = "BuyMyPhone Deep Analysis Report",
                reportText = deepReportText
            )

            Toast.makeText(
                this,
                if (success) "PDF report saved successfully" else "Failed to save PDF report",
                Toast.LENGTH_SHORT
            ).show()
        }

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

        binding.btnSaveDeepPdf.setOnClickListener {
            if (::deepReportText.isInitialized) {
                createPdfDocumentLauncher.launch(
                    "deep_analysis_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
                )
            } else {
                Toast.makeText(this, "Run deep analysis first", Toast.LENGTH_SHORT).show()
            }
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
        analysisResult = DeepAnalysisEngine.run(this, info)
        deepReportText = buildDeepReport(info, analysisResult)

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
            3 -> appendLog("[INFO] Hardware checks:\n${analysisResult.hardwareStarterVerdict}")
            4 -> appendLog("[INFO] Sensor checks:\n${analysisResult.sensorStarterVerdict}")
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

    private fun buildDeepReport(
        info: com.buymyphone.app.model.BasicDeviceInfo,
        result: DeepAnalysisResult
    ): String {
        return buildString {
            appendLine("BUYMYPHONE DEEP ANALYSIS REPORT")
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
            appendLine("Hardware Check Summary:")
            appendLine(result.hardwareStarterVerdict)
            appendLine()
            appendLine("Sensor Summary:")
            appendLine(result.sensorStarterVerdict)
            appendLine()
            appendLine("Core Device Info:")
            appendLine("RAM: ${"%.2f".format(Locale.US, info.totalRamGb)} GB")
            appendLine("Storage: ${"%.2f".format(Locale.US, info.totalStorageGb)} GB")
            appendLine("Display: ${info.displayWidth} x ${info.displayHeight}")
            appendLine("Refresh Rate: ${"%.2f".format(Locale.US, info.refreshRate.toDouble())} Hz")
            appendLine("Battery Level: ${info.batteryLevelPercent}%")
            appendLine("Battery Health: ${info.batteryHealthText}")
            appendLine("Battery Temperature: ${info.batteryTemperatureCelsius} °C")
            appendLine("SoC: ${info.socModel}")
            appendLine("GPU: ${info.gpuRenderer}")
            appendLine("Rear Cameras: ${info.rearCameraCount}")
            appendLine("Front Cameras: ${info.frontCameraCount}")
            appendLine("Best Rear Camera: ${"%.2f".format(Locale.US, info.bestRearCameraMp)} MP")
            appendLine("OIS: ${if (info.hasOis) "Yes" else "No"}")
            appendLine("4K Support: ${if (info.supports4k) "Yes" else "No"}")
            appendLine("Total Sensors: ${info.totalSensors}")
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
