package com.buymyphone.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.R
import com.buymyphone.app.databinding.ActivitySplashBinding
import com.buymyphone.app.ui.home.HomeActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())

    private val splashText = "Initializing secure phone analysis engine..."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startSplashEffects()
    }

    private fun startSplashEffects() {
        val zoomAnim = AnimationUtils.loadAnimation(this, R.anim.splash_zoom)
        binding.imgLogo.startAnimation(zoomAnim)
        binding.viewGlow.startAnimation(zoomAnim)

        startTypingEffect()

        handler.postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 2600)
    }

    private fun startTypingEffect() {
        binding.txtTagline.text = ""

        for (i in splashText.indices) {
            handler.postDelayed({
                binding.txtTagline.text = splashText.substring(0, i + 1)
            }, (i * 35).toLong())
        }
    }
}
