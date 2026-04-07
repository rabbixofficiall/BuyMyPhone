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

        binding.btnBackPremium.setOnClickListener {
            finish()
        }

        binding.btnUnlockPremium.setOnClickListener {
            binding.txtPremiumStatus.text = "Billing system will be connected in the next batch."
        }
    }
}
