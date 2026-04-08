package com.buymyphone.app.ui.result

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadResult()

        binding.btnBackHomeFromResult.setOnClickListener {
            finish()
        }
    }

    private fun loadResult() {
        val manufacturer = Build.MANUFACTURER ?: "Unknown"
        val model = Build.MODEL ?: "Unknown"
        val androidVersion = Build.VERSION.RELEASE ?: "Unknown"

        val score = calculateScore()

        binding.circularScore.setScoreAnimated(score)

        binding.txtVerdict.text = """
Device: $manufacturer $model

Android Version: $androidVersion

Overall Score: $score / 100

${getVerdict(score)}
        """.trimIndent()
    }

    private fun calculateScore(): Int {
        val sdk = Build.VERSION.SDK_INT

        return when {
            sdk >= 34 -> 95
            sdk >= 33 -> 90
            sdk >= 31 -> 84
            sdk >= 29 -> 78
            sdk >= 26 -> 70
            else -> 60
        }
    }

    private fun getVerdict(score: Int): String {
        return when {
            score >= 90 -> "Excellent device for heavy usage."
            score >= 80 -> "Very good device for gaming and daily use."
            score >= 70 -> "Good device for normal daily tasks."
            else -> "Basic device for light use."
        }
    }
}
