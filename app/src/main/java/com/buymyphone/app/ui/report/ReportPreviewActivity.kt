package com.buymyphone.app.ui.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityReportPreviewBinding

class ReportPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reportText = intent.getStringExtra("report_text") ?: buildDefaultReport()

        binding.txtReportContent.text = reportText

        binding.btnClosePreview.setOnClickListener {
            finish()
        }
    }

    private fun buildDefaultReport(): String {
        return buildString {
            appendLine("BUYMYPHONE REPORT")
            appendLine("----------------------------")
            appendLine("Overall Score: 69/100")
            appendLine()
            appendLine("Item Scores:")
            appendLine("CPU: 60/100")
            appendLine("GPU: 58/100")
            appendLine("RAM: 72/100")
            appendLine("Storage: 65/100")
            appendLine("Display: 80/100")
            appendLine("Battery: 70/100")
            appendLine("Camera: 62/100")
            appendLine("Sensors: 85/100")
            appendLine()
            appendLine("Raw Info:")
            appendLine("Model: Example Device")
            appendLine("Android Version: 13")
            appendLine("RAM: 8 GB")
            appendLine("Storage: 128 GB")
            appendLine("Display: 1080 x 2400, 120Hz")
            appendLine("Processor: Snapdragon 778G")
            appendLine("GPU: Adreno 642L")
            appendLine("Battery: 4300 mAh")
            appendLine("Camera: 64 MP main")
            appendLine("Sensors Status: Most major sensors available")
            appendLine()
            appendLine("Verdict:")
            appendLine("Very good phone for daily use, multitasking, and moderate gaming.")
        }
    }
}
