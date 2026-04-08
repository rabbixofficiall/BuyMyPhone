package com.buymyphone.app.ui.premium

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.billing.BillingManager
import com.buymyphone.app.databinding.ActivityPremiumBinding

class PremiumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPremiumBinding
    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billingManager = BillingManager(
            context = this,
            onPremiumPurchased = {
                binding.txtPremiumStatus.text = """
• Premium is active
• Ads can be removed
• Advanced reports can be enabled
• Extra score modules are ready
                """.trimIndent()
            },
            onMessage = { message ->
                binding.txtPremiumStatus.text = message
            }
        )

        billingManager.startConnection {
            if (billingManager.isPremiumUser()) {
                binding.txtPremiumStatus.text = """
• Premium already active
• Ad-free experience available
• Detailed reports unlocked
• Extra score modules unlocked
                """.trimIndent()
            }
        }

        binding.btnUnlockPremium.setOnClickListener {
            billingManager.launchPremiumPurchase(this)
        }

        binding.btnBackPremium.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.endConnection()
    }
}
