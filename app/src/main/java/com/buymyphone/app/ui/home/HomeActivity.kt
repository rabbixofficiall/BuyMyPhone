package com.buymyphone.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.R
import com.buymyphone.app.databinding.ActivityHomeBinding
import com.buymyphone.app.ui.about.AboutActivity
import com.buymyphone.app.ui.analysis.AnalysisActivity
import com.buymyphone.app.ui.premium.PremiumActivity
import com.buymyphone.app.ui.settings.SettingsActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarHome.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.menu_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.menu_premium -> {
                    startActivity(Intent(this, PremiumActivity::class.java))
                    true
                }
                else -> false
            }
        }

        binding.btnStartAnalysis.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            startActivity(Intent(this, AnalysisActivity::class.java))
        }
    }
}
