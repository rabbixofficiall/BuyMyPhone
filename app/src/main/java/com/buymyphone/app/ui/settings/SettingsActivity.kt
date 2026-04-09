package com.buymyphone.app.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivitySettingsBinding
import com.buymyphone.app.settings.SettingsManager
import com.buymyphone.app.ui.compare.ComparePhonesActivity
import com.buymyphone.app.ui.heavyapps.HeavyAppsDetectorActivity
import com.buymyphone.app.ui.premium.PremiumActivity
import com.buymyphone.app.ui.reports.ReportsActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switchDarkMode.isChecked = SettingsManager.isDarkMode(this)

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.setDarkMode(this, isChecked)
            recreate()
        }

        binding.btnOpenReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        binding.btnComparePhones.setOnClickListener {
            startActivity(Intent(this, ComparePhonesActivity::class.java))
        }

        binding.btnHeavyAppsDetector.setOnClickListener {
            startActivity(Intent(this, HeavyAppsDetectorActivity::class.java))
        }

        binding.btnGoPremiumSettings.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }

        binding.btnBackSettings.setOnClickListener {
            finish()
        }
    }
}
