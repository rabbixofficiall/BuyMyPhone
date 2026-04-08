package com.buymyphone.app.ui.analysis

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.R
import com.buymyphone.app.databinding.ActivityAnalysisBinding
import com.buymyphone.app.ui.result.ResultActivity

class AnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysisBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelAnalysis.setOnClickListener {
            finish()
        }

        startFakeAnalysis()
    }

    private fun startFakeAnalysis() {
        var progress = 0

        val runnable = object : Runnable {
            override fun run() {
                if (progress <= 100) {
                    binding.progressAnalysis.progress = progress
                    binding.txtProgressPercent.text = "$progress%"
                    binding.txtCurrentStatus.text = "Scanning system... ($progress%)"
                    binding.txtLiveLog.append("Step $progress completed...\n")

                    progress += 5
                    handler.postDelayed(this, 120)
                } else {
                    goToResult()
                }
            }
        }

        handler.post(runnable)
    }

    private fun goToResult() {
        startActivity(Intent(this, ResultActivity::class.java))
        overridePendingTransition(R.anim.fade_slide, android.R.anim.fade_out)
        finish()
    }
}
