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

        binding.txtReportContent.text =
            intent.getStringExtra("report_text") ?: "No report found"

        binding.btnClosePreview.setOnClickListener {
            finish()
        }
    }
}
