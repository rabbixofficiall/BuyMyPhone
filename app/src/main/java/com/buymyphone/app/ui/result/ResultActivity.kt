package com.buymyphone.app.ui.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityResultBinding
import com.buymyphone.app.detector.DeviceInfoDetector
import com.buymyphone.app.export.TxtReportExporter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val basicInfo = DeviceInfoDetector.getBasicDeviceInfo(this)

        val cpuScore = 60
        val gpuScore = 58
        val ramScore = getRamScore(basicInfo.totalRamGb)
        val storageScore = getStorageScore(basicInfo.totalStorageGb)
        val displayScore = 80
        val batteryScore = 70
        val cameraScore = 62
        val sensorScore = 85

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
            appendLine("Display: Not added yet")
            appendLine("Processor: Not added yet")
            appendLine("GPU: Not added yet")
            appendLine("Battery: Not added yet")
            appendLine("Camera: Not added yet")
            appendLine("Sensors Status: Not added yet")
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
            appendLine("Model: $modelName")
            appendLine("Android Version: ${basicInfo.androidVersion}")
            appendLine("SDK: ${basicInfo.sdkInt}")
            appendLine("Security Patch: ${basicInfo.securityPatch}")
            appendLine("RAM: ${DeviceFormatUtils.formatDouble(basicInfo.totalRamGb)} GB")
            appendLine("Available RAM: ${DeviceFormatUtils.formatDouble(basicInfo.availableRamGb)} GB")
            appendLine("Storage: ${DeviceFormatUtils.formatDouble(basicInfo.totalStorageGb)} GB")
            appendLine("Available Storage: ${DeviceFormatUtils.formatDouble(basicInfo.availableStorageGb)} GB")
            appendLine("Display: Not added yet")
            appendLine("Processor: Not added yet")
            appendLine("GPU: Not added yet")
            appendLine("Battery: Not added yet")
            appendLine("Camera: Not added yet")
            appendLine("Sensors Status: Not added yet")
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
            Toast.makeText(this, "PDF download will be added in next step", Toast.LENGTH_SHORT).show()
        }

        binding.btnBackHomeFromResult.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
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

    private fun getVerdict(score: Int): String {
        return when {
            score >= 85 -> "Excellent phone for gaming, camera, and heavy daily use."
            score >= 70 -> "Very good phone for daily use, multitasking, and moderate gaming."
            score >= 55 -> "Average phone for normal daily tasks and light gaming."
            else -> "Basic phone, better for simple use only."
        }
    }
}
