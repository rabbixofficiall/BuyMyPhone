package com.buymyphone.app.ui.premium

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityPremiumBinding

class PremiumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPremiumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtPremiumStatus.text = """
• Premium section is ready
• Ad-free mode can be added later
• Advanced reports can be added later
• Extra score modules can be added later
        """.trimIndent()

        binding.btnUnlockPremium.setOnClickListener {
            binding.txtPremiumStatus.text = """
• Premium purchase system is not connected yet
• This screen is prepared for future upgrade
• You can add billing later without changing UI
            """.trimIndent()
        }

        binding.btnBackPremium.setOnClickListener {
            finish()
        }
    }
}
