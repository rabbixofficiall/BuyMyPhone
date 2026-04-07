package com.buymyphone.app.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityHomeBinding
import com.buymyphone.app.ui.about.AboutActivity
import com.buymyphone.app.ui.analysis.AnalysisActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartAnalysis.setOnClickListener {
            startActivity(Intent(this, AnalysisActivity::class.java))
        }

        binding.btnAboutApp.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}
