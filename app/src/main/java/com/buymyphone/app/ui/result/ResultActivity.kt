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
import com.buymyphone.app.export.TxtReportExporter
import com.buymyphone.app.matcher.GpuMatcher
import com.buymyphone.app.matcher.SocMatcher
import com.buymyphone.app.ui.home.HomeActivity
import com.buymyphone.app.ui.report.ReportPreviewActivity
import com.buymyphone.app.utils.DeviceFormatUtils

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var reportText: String

    private val createTxtDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
            if (uri == null) {
                Toast.makeText(this, "TXT save cancelled", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val success = TxtReportExporter.export(this, uri, reportText)
            if (success) {
                Toast.makeText(this, "TXT report saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save TXT report", Toast.LENGTH_SHORT).show()
            }
        }

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

            if (success) {
                Toast.makeText(this, "PDF report saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save PDF report", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val basicInfo = DeviceInfoDetector.getBasicDeviceInfo(this)

        val matchedSoc = SocMatcher.findBestSocMatch(this, basicInfo.socModel)
        val matchedGpu = GpuMatcher.findBestGpuMatch(this, basicInfo.gpuRenderer)

        val cpuScore = matchedSoc?.cpuScore ?: getCpuScoreFallback(
            coreCount = basicInfo.coreCount,
            maxCpuFreqMHz = basicInfo.maxCpuFreqMHz,
            socModel = basicInfo.socModel
        )

        val gpuScore = matchedGpu?.score ?: matchedSoc?.gpuScore ?: getGpuScoreFallback(
            gpuRenderer = basicInfo.gpuRenderer,
            gpuVersion = basicInfo.gpuVersion
        )

        val ramScore = getRamScore(basicInfo.totalRamGb)
        val storageScore = getStorageScore(basicInfo.totalStorageGb)

        val displayScore = getDisplayScore(
            width = basicInfo.displayWidth,
            height = basicInfo.displayHeight,
            refreshRate = basicInfo.refreshRate
        )

        val batteryScore = matchedSoc?.batteryScore ?: getBatteryScore(
            batteryLevelPercent = basicInfo.batteryLevelPercent,
            batteryHealthText = basicInfo.batteryHealthText,
            isCharging = basicInfo.isCharging
        )

        val cameraScore = matchedSoc?.cameraScore ?: getCameraScore(
            rearCameraCount = basicInfo.rearCameraCount,
            bestRearCameraMp = basicInfo.bestRearCameraMp,
            hasOis = basicInfo.hasOis,
            hasAutoFocus = basicInfo.hasAutoFocus,
            hasVideoStabilization = basicInfo.hasVideoStabilization,
            supportsRaw = basicInfo.supportsRaw,
            supports4k = basicInfo.supports4k
        )

        val sensorScore = getSensorScore(
            hasAccelerometer = basicInfo.hasAccelerometer,
            hasGyroscope = basicInfo.hasGyroscope,
            hasProximity = basicInfo.hasProximity,
            hasLightSensor = basicInfo.hasLightSensor,
            hasMagneticField = basicInfo.hasMagneticField,
            hasBarometer = basicInfo.hasBarometer,
            hasStepCounter = basicInfo.hasStepCounter,
            hasRotationVector = basicInfo.hasRotationVector,
            hasHeartRate = basicInfo.hasHeartRate
        )

        val overallScore = (
            cpuScore +
            gpuScore +
            ramScore +
            storageScore +
            displayScore +
            batteryScore +
            cameraScore +
            sensorScore
        ) / 8

        val modelName = "${Build.BRAND} ${Build.MODEL}"

        binding.txtOverallScore.text = "$overallScore/100"
        binding.progressOverall.progress = overallScore
        binding.txtVerdict.text = getVerdict(overallScore)

        binding.txtItemScores.text = buildString {
            appendLine("CPU: $cpuScore/100")
            appendLine("GPU: $gpuScore/100")
            appendLine("RAM: $ramScore/100")
            appendLine("Storage: $storageScore/100")
            appendLine("Display: $displayScore/100")
            appendLine("Battery: $batteryScore/100")
            appendLine("Camera: $cameraScore/100")
            appendLine("Sensors: $sensorScore/100")
        }

        binding.txtRawInfo.text = buildString {
            appendLine("Model: $modelName")
            appendLine("Android Version: ${basicInfo.androidVersion}")
            appendLine("SDK: ${basicInfo.sdkInt}")
            appendLine("Security Patch: ${basicInfo.securityPatch}")
            appendLine("RAM: ${DeviceFormatUtils.formatDouble(basicInfo.totalRamGb)} GB")
            appendLine("Available RAM: ${DeviceFormatUtils.formatDouble(basicInfo.availableRamGb)} GB")
            appendLine("Storage: ${DeviceFormatUtils.formatDouble(basicInfo.totalStorageGb)} GB")
            appendLine("Available Storage: ${DeviceFormatUtils.formatDouble(basicInfo.availableStorageGb)} GB")
            appendLine("Display: ${basicInfo.displayWidth} x ${basicInfo.displayHeight}")
            appendLine("Refresh Rate: ${DeviceFormatUtils.formatDouble(basicInfo.refreshRate.toDouble())} Hz")
            appendLine("Density: ${basicInfo.densityDpi} dpi")
            appendLine("Battery Level: ${basicInfo.batteryLevelPercent}%")
            appendLine("Battery Temp: ${DeviceFormatUtils.formatDouble(basicInfo.batteryTemperatureCelsius.toDouble())} °C")
            appendLine("Charging: ${if (basicInfo.isCharging) "Yes" else "No"}")
            appendLine("Battery Health: ${basicInfo.batteryHealthText}")
            appendLine("SoC Model: ${basicInfo.socModel}")
            appendLine("SoC Manufacturer: ${basicInfo.socManufacturer}")
            appendLine("Matched SoC Preset: ${matchedSoc?.name ?: "No exact match"}")
            appendLine("Hardware: ${basicInfo.hardware}")
            appendLine("Board: ${basicInfo.board}")
            appendLine("CPU Cores: ${basicInfo.coreCount}")
            appendLine("Max CPU Frequency: ${basicInfo.maxCpuFreqMHz} MHz")
            appendLine("Supported ABIs: ${basicInfo.supportedAbis}")
            appendLine("GPU Renderer: ${basicInfo.gpuRenderer}")
            appendLine("GPU Vendor: ${basicInfo.gpuVendor}")
            appendLine("GPU Version: ${basicInfo.gpuVersion}")
            appendLine("Matched GPU Preset: ${matchedGpu?.name ?: "No exact match"}")
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
            appendLine("Total Cameras: ${basicInfo.totalCameras}")
            appendLine("Rear Cameras: ${basicInfo.rearCameraCount}")
            appendLine("Front Cameras: ${basicInfo.frontCameraCount}")
            appendLine("Best Rear Camera: ${DeviceFormatUtils.formatDouble(basicInfo.bestRearCameraMp)} MP")
            appendLine("Best Front Camera: ${DeviceFormatUtils.formatDouble(basicInfo.bestFrontCameraMp)} MP")
            appendLine("Flash: ${yesNo(basicInfo.hasFlash)}")
            appendLine("OIS: ${yesNo(basicInfo.hasOis)}")
            appendLine("Autofocus: ${yesNo(basicInfo.hasAutoFocus)}")
            appendLine("Video Stabilization: ${yesNo(basicInfo.hasVideoStabilization)}")
            appendLine("RAW Support: ${yesNo(basicInfo.supportsRaw)}")
            appendLine("4K Recording: ${yesNo(basicInfo.supports4k)}")
        }

        reportText = buildString {
            appendLine("BUYMYPHONE REPORT")
            appendLine("----------------------------")
            appendLine("Overall Score: $overallScore/100")
            appendLine()
            appendLine("Item Scores:")
            appendLine("CPU: $cpuScore/100")
            appendLine("GPU: $gpuScore/100")
            appendLine("RAM: $ramScore/100")
            appendLine("Storage: $storageScore/100")
            appendLine("Display: $displayScore/100")
            appendLine("Battery: $batteryScore/100")
            appendLine("Camera: $cameraScore/100")
            appendLine("Sensors: $sensorScore/100")
            appendLine()
            appendLine("Raw Info:")
            appendLine(binding.txtRawInfo.text.toString())
            appendLine()
            appendLine("Camera Summary:")
            basicInfo.cameraSummaryLines.forEach { appendLine(it) }
            appendLine()
            appendLine("Sensor Names:")
            basicInfo.sensorNames.forEach { appendLine(it) }
            appendLine()
            appendLine("Verdict:")
            appendLine(getVerdict(overallScore))
        }

        binding.btnPreviewReport.setOnClickListener {
            val intent = Intent(this, ReportPreviewActivity::class.java)
            intent.putExtra("report_text", reportText)
            startActivity(intent)
        }

        binding.btnDownloadTxt.setOnClickListener {
            createTxtDocumentLauncher.launch("buymyphone_report.txt")
        }

        binding.btnDownloadPdf.setOnClickListener {
            createPdfDocumentLauncher.launch("buymyphone_report.pdf")
        }

        binding.btnBackHomeFromResult.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun getCpuScoreFallback(coreCount: Int, maxCpuFreqMHz: Int, socModel: String): Int {
        if (socModel.contains("Snapdragon 8", ignoreCase = true)) return 94
        if (socModel.contains("Snapdragon 7", ignoreCase = true)) return 78
        if (socModel.contains("Snapdragon 6", ignoreCase = true)) return 62
        if (socModel.contains("Dimensity 8", ignoreCase = true)) return 86
        if (socModel.contains("Dimensity 9", ignoreCase = true)) return 92
        if (socModel.contains("Helio P22", ignoreCase = true)) return 24

        val coreScore = when {
            coreCount >= 8 -> 75
            coreCount >= 6 -> 62
            coreCount >= 4 -> 48
            else -> 30
        }

        val freqScore = when {
            maxCpuFreqMHz >= 3200 -> 95
            maxCpuFreqMHz >= 2800 -> 85
            maxCpuFreqMHz >= 2400 -> 72
            maxCpuFreqMHz >= 2000 -> 60
            maxCpuFreqMHz > 0 -> 45
            else -> 35
        }

        return (coreScore + freqScore) / 2
    }

    private fun getGpuScoreFallback(gpuRenderer: String, gpuVersion: String): Int {
        if (gpuRenderer.contains("Adreno 750", ignoreCase = true)) return 95
        if (gpuRenderer.contains("Adreno 740", ignoreCase = true)) return 92
        if (gpuRenderer.contains("Adreno 730", ignoreCase = true)) return 88
        if (gpuRenderer.contains("Adreno 642", ignoreCase = true)) return 74
        if (gpuRenderer.contains("Mali-G710", ignoreCase = true)) return 84
        if (gpuRenderer.contains("Mali-G610", ignoreCase = true)) return 74
        if (gpuRenderer.contains("Mali-G57", ignoreCase = true)) return 50
        if (gpuRenderer.contains("PowerVR GE8320", ignoreCase = true)) return 18

        return when {
            gpuVersion.contains("OpenGL ES 3.2", ignoreCase = true) -> 72
            gpuVersion.contains("OpenGL ES 3.1", ignoreCase = true) -> 62
            gpuVersion.contains("OpenGL ES 3.0", ignoreCase = true) -> 52
            gpuVersion.contains("OpenGL ES 2.0", ignoreCase = true) -> 35
            else -> 30
        }
    }

    private fun getRamScore(totalRamGb: Double): Int {
        return when {
            totalRamGb >= 16 -> 95
            totalRamGb >= 12 -> 88
            totalRamGb >= 8 -> 78
            totalRamGb >= 6 -> 68
            totalRamGb >= 4 -> 55
            else -> 35
        }
    }

    private fun getStorageScore(totalStorageGb: Double): Int {
        return when {
            totalStorageGb >= 512 -> 95
            totalStorageGb >= 256 -> 85
            totalStorageGb >= 128 -> 75
            totalStorageGb >= 64 -> 60
            else -> 40
        }
    }

    private fun getDisplayScore(width: Int, height: Int, refreshRate: Float): Int {
        val resolutionScore = when {
            width >= 1440 || height >= 3200 -> 90
            width >= 1080 || height >= 2400 -> 78
            width >= 720 || height >= 1600 -> 62
            else -> 45
        }

        val refreshScore = when {
            refreshRate >= 144f -> 95
            refreshRate >= 120f -> 88
            refreshRate >= 90f -> 76
            refreshRate >= 60f -> 60
            else -> 40
        }

        return (resolutionScore + refreshScore) / 2
    }

    private fun getBatteryScore(
        batteryLevelPercent: Int,
        batteryHealthText: String,
        isCharging: Boolean
    ): Int {
        var score = when {
            batteryLevelPercent >= 90 -> 90
            batteryLevelPercent >= 70 -> 80
            batteryLevelPercent >= 50 -> 68
            batteryLevelPercent >= 30 -> 55
            else -> 40
        }

        if (batteryHealthText.equals("Good", ignoreCase = true)) score += 5
        if (isCharging) score += 2

        return score.coerceAtMost(100)
    }

    private fun getCameraScore(
        rearCameraCount: Int,
        bestRearCameraMp: Double,
        hasOis: Boolean,
        hasAutoFocus: Boolean,
        hasVideoStabilization: Boolean,
        supportsRaw: Boolean,
        supports4k: Boolean
    ): Int {
        var score = when {
            bestRearCameraMp >= 108 -> 80
            bestRearCameraMp >= 64 -> 72
            bestRearCameraMp >= 48 -> 65
            bestRearCameraMp >= 16 -> 55
            bestRearCameraMp > 0 -> 45
            else -> 20
        }

        if (rearCameraCount >= 3) score += 6
        if (hasOis) score += 8
        if (hasAutoFocus) score += 5
        if (hasVideoStabilization) score += 5
        if (supportsRaw) score += 4
        if (supports4k) score += 5

        return score.coerceAtMost(100)
    }

    private fun getSensorScore(
        hasAccelerometer: Boolean,
        hasGyroscope: Boolean,
        hasProximity: Boolean,
        hasLightSensor: Boolean,
        hasMagneticField: Boolean,
        hasBarometer: Boolean,
        hasStepCounter: Boolean,
        hasRotationVector: Boolean,
        hasHeartRate: Boolean
    ): Int {
        var score = 0
        if (hasAccelerometer) score += 12
        if (hasGyroscope) score += 15
        if (hasProximity) score += 10
        if (hasLightSensor) score += 10
        if (hasMagneticField) score += 12
        if (hasBarometer) score += 10
        if (hasStepCounter) score += 8
        if (hasRotationVector) score += 13
        if (hasHeartRate) score += 10

        return score.coerceAtMost(100)
    }

    private fun getVerdict(score: Int): String {
        return when {
            score >= 85 -> "Excellent phone for gaming, camera, and heavy daily use."
            score >= 70 -> "Very good phone for daily use, multitasking, and moderate gaming."
            score >= 55 -> "Average phone for normal daily tasks and light gaming."
            else -> "Basic phone, better for simple use only."
        }
    }

    private fun yesNo(value: Boolean): String {
        return if (value) "Yes" else "No"
    }
}
