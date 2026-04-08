package com.buymyphone.app.ui.result

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityResultBinding
import com.buymyphone.app.export.PdfReportExporter
import com.buymyphone.app.export.TxtReportExporter
import com.buymyphone.app.ui.report.ReportPreviewActivity

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
            Toast.makeText(
                this,
                if (success) "TXT report saved successfully" else "Failed to save TXT report",
                Toast.LENGTH_SHORT
            ).show()
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

        loadResult()

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
            finish()
        }
    }

    private fun loadResult() {
        val manufacturer = Build.MANUFACTURER ?: "Unknown"
        val model = Build.MODEL ?: "Unknown"
        val androidVersion = Build.VERSION.RELEASE ?: "Unknown"

        val score = calculateScore()

        binding.circularScore.setScoreAnimated(score)

        val verdictText = """
Device: $manufacturer $model

Android Version: $androidVersion

Overall Score: $score / 100

${getVerdict(score)}
        """.trimIndent()

        binding.txtVerdict.text = verdictText
        reportText = buildReportText(manufacturer, model, androidVersion, score)
    }

    private fun calculateScore(): Int {
        val sdk = Build.VERSION.SDK_INT
        return when {
            sdk >= 34 -> 95
            sdk >= 33 -> 90
            sdk >= 31 -> 84
            sdk >= 29 -> 78
            sdk >= 26 -> 70
            else -> 60
        }
    }

    private fun getVerdict(score: Int): String {
        return when {
            score >= 90 -> "Excellent device for heavy usage."
            score >= 80 -> "Very good device for gaming and daily use."
            score >= 70 -> "Good device for normal daily tasks."
            else -> "Basic device for light use."
        }
    }

    private fun buildReportText(
        manufacturer: String,
        model: String,
        androidVersion: String,
        score: Int
    ): String {
        return buildString {
            appendLine("BUYMYPHONE REPORT")
            appendLine("----------------------------")
            appendLine("Manufacturer: $manufacturer")
            appendLine("Model: $model")
            appendLine("Android Version: $androidVersion")
            appendLine("SDK: ${Build.VERSION.SDK_INT}")
            appendLine("Overall Score: $score/100")
            appendLine()
            appendLine("Verdict:")
            appendLine(getVerdict(score))
            appendLine()
            appendLine("Build Info:")
            appendLine("Brand: ${Build.BRAND}")
            appendLine("Device: ${Build.DEVICE}")
            appendLine("Hardware: ${Build.HARDWARE}")
            appendLine("Board: ${Build.BOARD}")
            appendLine("Product: ${Build.PRODUCT}")
            appendLine("Supported ABIs: ${Build.SUPPORTED_ABIS.joinToString()}")
        }
    }
}
