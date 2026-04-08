package com.buymyphone.app.ui.result

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityResultBinding
import com.buymyphone.app.detector.DeviceInfoDetector
import com.buymyphone.app.export.PdfReportExporter
import com.buymyphone.app.matcher.GpuMatcher
import com.buymyphone.app.matcher.SocMatcher
import com.buymyphone.app.scoring.PresetRuleEngine
import com.buymyphone.app.storage.ReportHistoryManager
import com.buymyphone.app.ui.report.ReportPreviewActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var reportText: String

    private val createPdfDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
            if (uri == null) {
                Toast.makeText(this, "PDF save cancelled", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val success = PdfReportExporter.export(
                context = this,
                uri = uri,
                title = "BuyMyPhone Report",
                reportText = reportText
            )

            Toast.makeText(
                this,
                if (success) "PDF report saved successfully" else "Failed to save PDF report",
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buildResult()

        binding.btnPreviewReport.setOnClickListener {
            val intent = Intent(this, ReportPreviewActivity::class.java)
            intent.putExtra("report_text", reportText)
            startActivity(intent)
        }

        binding.btnSavePdf.setOnClickListener {
            createPdfDocumentLauncher.launch("buymyphone_report.pdf")
        }

        binding.btnBackHomeFromResult.setOnClickListener {
            finish()
        }
    }

    private fun buildResult() {
        val basicInfo = DeviceInfoDetector.getBasicDeviceInfo(this)
        val matchedSoc = SocMatcher.findBestSocMatch(this, basicInfo.socModel)
        val matchedGpu = GpuMatcher.findBestGpuMatch(this, basicInfo.gpuRenderer)

        val displayPreset = PresetRuleEngine.getDisplayPreset(
            basicInfo.displayWidth,
            basicInfo.displayHeight,
            basicInfo.refreshRate
        )
        val storagePreset = PresetRuleEngine.getStoragePreset(
            basicInfo.totalStorageGb,
            basicInfo.socModel
        )
        val cameraPreset = PresetRuleEngine.getCameraPreset(
            basicInfo.bestRearCameraMp,
            basicInfo.hasOis,
            basicInfo.hasAutoFocus,
            basicInfo.hasVideoStabilization,
            basicInfo.supports4k
        )

        val cpuScore = matchedSoc?.cpuScore ?: fallbackCpuScore(basicInfo.socModel, basicInfo.coreCount)
        val gpuScore = matchedGpu?.score ?: matchedSoc?.gpuScore ?: fallbackGpuScore(basicInfo.gpuRenderer)
        val ramScore = getRamScore(basicInfo.totalRamGb)
        val storageScore = storagePreset.score
        val displayScore = displayPreset.score
        val batteryScore = matchedSoc?.batteryScore ?: getBatteryScore(basicInfo.batteryLevelPercent, basicInfo.batteryHealthText)
        val cameraScore = cameraPreset.score
        val sensorScore = getSensorScore(
            basicInfo.hasAccelerometer,
            basicInfo.hasGyroscope,
            basicInfo.hasProximity,
            basicInfo.hasLightSensor,
            basicInfo.hasMagneticField,
            basicInfo.hasBarometer,
            basicInfo.hasStepCounter,
            basicInfo.hasRotationVector,
            basicInfo.hasHeartRate
        )

        val overallScore = (cpuScore + gpuScore + ramScore + storageScore + displayScore + batteryScore + cameraScore + sensorScore) / 8

        val gamingScore = (cpuScore + gpuScore + displayScore + ramScore) / 4
        val cameraUseScore = (cameraScore + displayScore + cpuScore) / 3
        val dailyUseScore = (batteryScore + ramScore + storageScore + cpuScore) / 4
        val multitaskingScore = (ramScore + storageScore + cpuScore) / 3

        val bestPurpose = buildBestPurpose(gamingScore, cameraUseScore, dailyUseScore, multitaskingScore)

        binding.circularScore.setScoreAnimated(overallScore)
        binding.txtVerdict.text = bestPurpose

        reportText = buildDetailedReport(
            basicInfo = basicInfo,
            matchedSocName = matchedSoc?.name ?: "No exact preset match",
            matchedGpuName = matchedGpu?.name ?: "No exact preset match",
            displayPresetLabel = displayPreset.label,
            storagePresetLabel = storagePreset.label,
            cameraPresetLabel = cameraPreset.label,
            overallScore = overallScore,
            cpuScore = cpuScore,
            gpuScore = gpuScore,
            ramScore = ramScore,
            storageScore = storageScore,
            displayScore = displayScore,
            batteryScore = batteryScore,
            cameraScore = cameraScore,
            sensorScore = sensorScore,
            bestPurpose = bestPurpose
        )

        val title = "${Build.MANUFACTURER} ${Build.MODEL} - ${
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        }"
        ReportHistoryManager.saveReport(this, title, reportText)
    }

    private fun buildBestPurpose(
        gamingScore: Int,
        cameraUseScore: Int,
        dailyUseScore: Int,
        multitaskingScore: Int
    ): String {
        val max = maxOf(gamingScore, cameraUseScore, dailyUseScore, multitaskingScore)
        return when (max) {
            gamingScore -> "Best for Gaming and performance-focused usage."
            cameraUseScore -> "Best for Camera, content creation and media use."
            dailyUseScore -> "Best for Daily Use, stable battery and smooth operation."
            else -> "Best for Multitasking and productivity-focused work."
        }
    }

    private fun buildDetailedReport(
        basicInfo: com.buymyphone.app.model.BasicDeviceInfo,
        matchedSocName: String,
        matchedGpuName: String,
        displayPresetLabel: String,
        storagePresetLabel: String,
        cameraPresetLabel: String,
        overallScore: Int,
        cpuScore: Int,
        gpuScore: Int,
        ramScore: Int,
        storageScore: Int,
        displayScore: Int,
        batteryScore: Int,
        cameraScore: Int,
        sensorScore: Int,
        bestPurpose: String
    ): String {
        return buildString {
            appendLine("BUYMYPHONE REPORT")
            appendLine("================================")
            appendLine("Best Use Summary:")
            appendLine(bestPurpose)
            appendLine()

            appendLine("Overall Score: $overallScore/100")
            appendLine()
            appendLine("Item Scores:")
            appendLine("CPU Score: $cpuScore/100")
            appendLine("GPU Score: $gpuScore/100")
            appendLine("RAM Score: $ramScore/100")
            appendLine("Storage Score: $storageScore/100")
            appendLine("Display Score: $displayScore/100")
            appendLine("Battery Score: $batteryScore/100")
            appendLine("Camera Score: $cameraScore/100")
            appendLine("Sensor Score: $sensorScore/100")
            appendLine()

            appendLine("Preset Classification:")
            appendLine("SoC Preset: $matchedSocName")
            appendLine("GPU Preset: $matchedGpuName")
            appendLine("Display Preset: $displayPresetLabel")
            appendLine("Storage Preset: $storagePresetLabel")
            appendLine("Camera Preset: $cameraPresetLabel")
            appendLine()

            appendLine("Device Information:")
            appendLine("Manufacturer: ${Build.MANUFACTURER}")
            appendLine("Model: ${Build.MODEL}")
            appendLine("Brand: ${Build.BRAND}")
            appendLine("Device: ${Build.DEVICE}")
            appendLine("Board: ${Build.BOARD}")
            appendLine("Hardware: ${Build.HARDWARE}")
            appendLine("Product: ${Build.PRODUCT}")
            appendLine("Android Version: ${basicInfo.androidVersion}")
            appendLine("SDK: ${basicInfo.sdkInt}")
            appendLine("Security Patch: ${basicInfo.securityPatch}")
            appendLine()

            appendLine("Memory and Storage:")
            appendLine("Total RAM: ${"%.2f".format(Locale.US, basicInfo.totalRamGb)} GB")
            appendLine("Available RAM: ${"%.2f".format(Locale.US, basicInfo.availableRamGb)} GB")
            appendLine("Total Storage: ${"%.2f".format(Locale.US, basicInfo.totalStorageGb)} GB")
            appendLine("Available Storage: ${"%.2f".format(Locale.US, basicInfo.availableStorageGb)} GB")
            appendLine("Storage Type Class: $storagePresetLabel")
            appendLine()

            appendLine("Display Information:")
            appendLine("Resolution: ${basicInfo.displayWidth} x ${basicInfo.displayHeight}")
            appendLine("Refresh Rate: ${"%.2f".format(Locale.US, basicInfo.refreshRate.toDouble())} Hz")
            appendLine("Density: ${basicInfo.densityDpi} dpi")
            appendLine("Display Type Class: $displayPresetLabel")
            appendLine()

            appendLine("Battery Information:")
            appendLine("Battery Level: ${if (basicInfo.batteryLevelPercent >= 0) "${basicInfo.batteryLevelPercent}%" else "Unknown"}")
            appendLine("Battery Temperature: ${if (basicInfo.batteryTemperatureCelsius >= 0) "${"%.2f".format(Locale.US, basicInfo.batteryTemperatureCelsius.toDouble())} °C" else "Unknown"}")
            appendLine("Charging: ${yesNo(basicInfo.isCharging)}")
            appendLine("Battery Health: ${basicInfo.batteryHealthText}")
            appendLine()

            appendLine("SoC / CPU / GPU:")
            appendLine("Detected SoC: ${basicInfo.socModel}")
            appendLine("Matched SoC Preset: $matchedSocName")
            appendLine("SoC Manufacturer: ${basicInfo.socManufacturer}")
            appendLine("CPU Cores: ${basicInfo.coreCount}")
            appendLine("Max CPU Frequency: ${basicInfo.maxCpuFreqMHz} MHz")
            appendLine("Supported ABIs: ${basicInfo.supportedAbis}")
            appendLine("GPU Renderer: ${basicInfo.gpuRenderer}")
            appendLine("GPU Vendor: ${basicInfo.gpuVendor}")
            appendLine("GPU Version: ${basicInfo.gpuVersion}")
            appendLine("Matched GPU Preset: $matchedGpuName")
            appendLine()

            appendLine("Camera Information:")
            appendLine("Total Cameras: ${basicInfo.totalCameras}")
            appendLine("Rear Cameras: ${basicInfo.rearCameraCount}")
            appendLine("Front Cameras: ${basicInfo.frontCameraCount}")
            appendLine("Best Rear Camera: ${"%.2f".format(Locale.US, basicInfo.bestRearCameraMp)} MP")
            appendLine("Best Front Camera: ${"%.2f".format(Locale.US, basicInfo.bestFrontCameraMp)} MP")
            appendLine("Flash: ${yesNo(basicInfo.hasFlash)}")
            appendLine("OIS: ${yesNo(basicInfo.hasOis)}")
            appendLine("Autofocus: ${yesNo(basicInfo.hasAutoFocus)}")
            appendLine("Video Stabilization: ${yesNo(basicInfo.hasVideoStabilization)}")
            appendLine("RAW Support: ${yesNo(basicInfo.supportsRaw)}")
            appendLine("4K Recording: ${yesNo(basicInfo.supports4k)}")
            appendLine("Camera Tier: $cameraPresetLabel")
            appendLine()
            appendLine("Camera Summary:")
            basicInfo.cameraSummaryLines.forEach { appendLine(it) }
            appendLine()

            appendLine("Sensors:")
            appendLine("Total Sensors: ${basicInfo.totalSensors}")
            appendLine("Accelerometer: ${yesNo(basicInfo.hasAccelerometer)}")
            appendLine("Gyroscope: ${yesNo(basicInfo.hasGyroscope)}")
            appendLine("Proximity: ${yesNo(basicInfo.hasProximity)}")
            appendLine("Light Sensor: ${yesNo(basicInfo.hasLightSensor)}")
            appendLine("Magnetic Field: ${yesNo(basicInfo.hasMagneticField)}")
            appendLine("Barometer: ${yesNo(basicInfo.hasBarometer)}")
            appendLine("Step Counter: ${yesNo(basicInfo.hasStepCounter)}")
            appendLine("Rotation Vector: ${yesNo(basicInfo.hasRotationVector)}")
            appendLine("Heart Rate: ${yesNo(basicInfo.hasHeartRate)}")
            appendLine()
            appendLine("Sensor Names:")
            basicInfo.sensorNames.forEach { appendLine(it) }
        }
    }

    private fun fallbackCpuScore(socModel: String, coreCount: Int): Int {
        if (socModel.contains("Snapdragon 8", true)) return 94
        if (socModel.contains("Snapdragon 7", true)) return 78
        if (socModel.contains("Dimensity 9", true)) return 92
        if (socModel.contains("Dimensity 8", true)) return 86
        if (socModel.contains("Helio G99", true)) return 58
        if (socModel.contains("Helio P22", true)) return 20

        return when {
            coreCount >= 8 -> 72
            coreCount >= 6 -> 60
            coreCount >= 4 -> 48
