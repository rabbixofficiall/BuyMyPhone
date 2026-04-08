package com.buymyphone.app.ui.result

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.R
import com.buymyphone.app.databinding.ActivityResultBinding
import com.buymyphone.app.detector.DeviceInfoDetector
import com.buymyphone.app.export.PdfReportExporter
import com.buymyphone.app.export.TxtReportExporter
import com.buymyphone.app.storage.ReportHistoryManager
import com.buymyphone.app.ui.report.ReportPreviewActivity
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var reportText: String

    private val createTxtLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult

            val success = TxtReportExporter.export(this, uri, reportText)
            Toast.makeText(this,
                if (success) "TXT saved" else "Failed",
                Toast.LENGTH_SHORT).show()
        }

    private val createPdfLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult

            val success = PdfReportExporter.export(
                this,
                uri,
                "BuyMyPhone Report",
                reportText
            )

            Toast.makeText(this,
                if (success) "PDF saved" else "Failed",
                Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.slide_up)
        )

        buildResult()

        binding.btnPreviewReport.setOnClickListener {
            val i = Intent(this, ReportPreviewActivity::class.java)
            i.putExtra("report_text", reportText)
            startActivity(i)
        }

        binding.btnDownloadTxt.setOnClickListener {
            createTxtLauncher.launch("report.txt")
        }

        binding.btnDownloadPdf.setOnClickListener {
            createPdfLauncher.launch("report.pdf")
        }

        binding.btnBackHomeFromResult.setOnClickListener {
            finish()
        }
    }

    private fun buildResult() {

        val info = DeviceInfoDetector.getBasicDeviceInfo(this)

        val score = calculateSimpleScore(info)

        binding.circularScore.setScoreAnimated(score)

        val bestUse = when {
            score >= 85 -> "Best for Gaming & Heavy Use"
            score >= 70 -> "Best for Daily Use"
            score >= 50 -> "Basic Usage"
            else -> "Not recommended"
        }

        binding.txtVerdict.text = bestUse

        reportText = generateReport(info, score, bestUse)

        val title = "${Build.MODEL} - ${
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        }"

        ReportHistoryManager.saveReport(this, title, reportText)
    }

    private fun calculateSimpleScore(info: com.buymyphone.app.model.BasicDeviceInfo): Int {

        var score = 0

        score += when {
            info.totalRamGb >= 8 -> 30
            info.totalRamGb >= 6 -> 25
            info.totalRamGb >= 4 -> 18
            else -> 10
        }

        score += when {
            info.totalStorageGb >= 256 -> 25
            info.totalStorageGb >= 128 -> 20
            info.totalStorageGb >= 64 -> 15
            else -> 10
        }

        score += when {
            info.refreshRate >= 120 -> 20
            info.refreshRate >= 90 -> 15
            else -> 10
        }

        score += when {
            info.batteryLevelPercent >= 80 -> 15
            info.batteryLevelPercent >= 50 -> 10
            else -> 5
        }

        return score.coerceAtMost(100)
    }

    private fun generateReport(
        info: com.buymyphone.app.model.BasicDeviceInfo,
        score: Int,
        bestUse: String
    ): String {

        return """
BUYMYPHONE REPORT
======================

Device: ${Build.MANUFACTURER} ${Build.MODEL}
Android: ${info.androidVersion}

Overall Score: $score

Best Use:
$bestUse

----------------------
DETAILS
----------------------

RAM: ${info.totalRamGb} GB
Storage: ${info.totalStorageGb} GB
Refresh Rate: ${info.refreshRate} Hz

Battery: ${info.batteryLevelPercent}%
Health: ${info.batteryHealthText}

SOC: ${info.socModel}
GPU: ${info.gpuRenderer}

Sensors: ${info.totalSensors}

======================
        """.trimIndent()
    }
}
