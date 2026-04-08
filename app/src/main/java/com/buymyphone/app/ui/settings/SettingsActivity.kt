package com.buymyphone.app.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivitySettingsBinding
import com.buymyphone.app.settings.SettingsManager
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

        binding.btnBackSettings.setOnClickListener {
            finish()
        }
    }
}
