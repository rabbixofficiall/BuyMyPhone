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
    }

    private fun loadResult() {

        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

        val score = calculateScore()

        binding.circularScore.setScoreAnimated(score)

        binding.txtVerdict.text = """
Device: $deviceName

Score: $score / 100

${getVerdict(score)}
        """.trimIndent()

        binding.btnBackHomeFromResult.setOnClickListener {
            finish()
        }
    }

    private fun calculateScore(): Int {
        val base = Build.VERSION.SDK_INT * 2
        return base.coerceIn(50, 100)
    }

    private fun getVerdict(score: Int): String {
        return when {
            score >= 90 -> "🔥 Flagship performance"
            score >= 75 -> "⚡ Very good device"
            score >= 60 -> "👍 Good for daily use"
            else -> "⚠️ Basic device"
        }
    }
}
