package com.buymyphone.app.ui.result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.R
import com.buymyphone.app.ads.AdManager
import com.buymyphone.app.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔥 Banner Ad Load
        val adView = AdManager.createBannerAd(
            this,
            getString(R.string.admob_banner_result)
        )
        AdManager.attachBanner(binding.adContainerResult, adView)

        // 🔥 Demo Data (later replace with real analysis result)
        binding.txtOverallScore.text = "82/100"
        binding.progressOverall.progress = 82
        binding.txtVerdict.text = "Very Good phone for daily use and moderate gaming."

        binding.txtItemScores.text = """
CPU: 80/100
GPU: 75/100
RAM: 85/100
Storage: 78/100
Display: 88/100
Battery: 82/100
Camera: 76/100
Sensors: 84/100
        """.trimIndent()

        binding.txtBestFor.text = """
Gaming: Very Good (80/100)
Camera: Good (76/100)
Daily Use: Excellent (85/100)
Multitasking: Very Good (83/100)

Top Recommendation: Best suited for daily use and multitasking.
        """.trimIndent()

        binding.txtRawInfo.text = """
Model: Demo Device
Android Version: 13
RAM: 8 GB
Storage: 128 GB
Display: 1080 x 2400
Refresh Rate: 120Hz
Battery: 5000 mAh
CPU: Snapdragon 778G
GPU: Adreno 642L
        """.trimIndent()

        // 🔘 Buttons
        binding.btnBackHomeFromResult.setOnClickListener {
            finish()
        }

        binding.btnPreviewReport.setOnClickListener {
            // future feature
        }

        binding.btnDownloadTxt.setOnClickListener {
            // future feature
        }

        binding.btnDownloadPdf.setOnClickListener {
            // future feature
        }
    }
}
