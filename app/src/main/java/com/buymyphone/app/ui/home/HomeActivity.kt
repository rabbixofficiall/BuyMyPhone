package com.buymyphone.app.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.R
import com.buymyphone.app.databinding.ActivityHomeBinding
import com.buymyphone.app.ui.about.AboutActivity
import com.buymyphone.app.ui.analysis.AnalysisActivity
import com.buymyphone.app.ui.premium.PremiumActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartAnalysis.setOnClickListener {
            startActivity(Intent(this, AnalysisActivity::class.java))
            overridePendingTransition(R.anim.fade_slide, android.R.anim.fade_out)
        }

        binding.btnAboutApp.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
            overridePendingTransition(R.anim.fade_slide, android.R.anim.fade_out)
        }

        binding.btnGoPremium.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
            overridePendingTransition(R.anim.fade_slide, android.R.anim.fade_out)
        }
    }
}
