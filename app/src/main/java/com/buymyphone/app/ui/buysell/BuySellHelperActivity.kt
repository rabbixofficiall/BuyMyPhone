package com.buymyphone.app.ui.buysell

import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.R
import com.buymyphone.app.databinding.ActivityBuySellHelperBinding
import com.buymyphone.app.export.PdfReportExporter

class BuySellHelperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuySellHelperBinding
    private lateinit var softwareReport: String
    private lateinit var hardwareReport: String

    private val saveSoftwarePdfLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
            if (uri == null) {
                Toast.makeText(this, "Software PDF save cancelled", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val success = PdfReportExporter.export(
                context = this,
                uri = uri,
                title = "BuyMyPhone Software Deep Analysis",
                reportText = softwareReport
            )

            Toast.makeText(
                this,
                if (success) "Software PDF saved successfully" else "Failed to save Software PDF",
                Toast.LENGTH_SHORT
            ).show()
        }

    private val saveHardwarePdfLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
            if (uri == null) {
                Toast.makeText(this, "Hardware PDF save cancelled", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val success = PdfReportExporter.export(
                context = this,
                uri = uri,
                title = "BuyMyPhone Hardware Manual Analysis",
                reportText = hardwareReport
            )

            Toast.makeText(
                this,
                if (success) "Hardware PDF saved successfully" else "Failed to save Hardware PDF",
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuySellHelperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rootBuySellHelper.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.slide_up)
        )

        val finalVerdict = intent.getStringExtra("final_verdict") ?: "No verdict"
        val buyReasons = intent.getStringExtra("buy_reasons") ?: "-"
        val avoidReasons = intent.getStringExtra("avoid_reasons") ?: "-"
        softwareReport = intent.getStringExtra("software_report") ?: "No software report"
        hardwareReport = intent.getStringExtra("hardware_report") ?: "No hardware report"

        binding.txtFinalVerdict.text = finalVerdict
        binding.txtBuyReasons.text = buyReasons
        binding.txtAvoidReasons.text = avoidReasons

        binding.btnSaveSoftwarePdf.setOnClickListener {
            saveSoftwarePdfLauncher.launch("software_deep_analysis.pdf")
        }

        binding.btnSaveHardwarePdf.setOnClickListener {
            saveHardwarePdfLauncher.launch("hardware_manual_analysis.pdf")
        }

        binding.btnBackBuySellHelper.setOnClickListener {
            finish()
        }
    }
}
