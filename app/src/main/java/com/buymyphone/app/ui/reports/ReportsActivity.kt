package com.buymyphone.app.ui.reports

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityReportsBinding
import com.buymyphone.app.storage.ReportHistoryManager
import com.buymyphone.app.ui.report.ReportPreviewActivity
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reports = ReportHistoryManager.getReports(this)

        val titles = if (reports.isEmpty()) {
            listOf("No saved reports yet")
        } else {
            val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            reports.map {
                "${it.title}\n${formatter.format(Date(it.timestamp))}"
            }
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            titles
        )

        binding.listReports.adapter = adapter

        binding.listReports.setOnItemClickListener { _, _, position, _ ->
            if (reports.isNotEmpty()) {
                val intent = Intent(this, ReportPreviewActivity::class.java)
                intent.putExtra("report_text", reports[position].content)
                startActivity(intent)
            }
        }

        binding.btnBackReports.setOnClickListener {
            finish()
        }
    }
}
