package com.buymyphone.app.ui.result

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityResultBinding
import com.buymyphone.app.ui.home.HomeActivity
import com.buymyphone.app.ui.report.ReportPreviewActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cpuScore = 60
        val gpuScore = 58
        val ramScore = 72
        val storageScore = 65
        val displayScore = 80
        val batteryScore = 70
        val cameraScore = 62
        val sensorScore = 85

        val overallScore = (cpuScore + gpuScore + ramScore + storageScore + displayScore + batteryScore + cameraScore + sensorScore) / 8

        binding.txtOverallScore.text = "$overallScore/100"
        binding.progressOverall.progress = overallScore
        binding.txtVerdict.text = getVerdict(overallScore)

        binding.txtItemScores.text = buildString {
            appendLine("CPU: $cpuScore/100")
            appendLine("GPU: $gpuScore/100")
            appendLine("RAM: $ramScore/100")
            appendLine("Storage: $storageScore/100")
            appendLine("Display: $displayScore/100")
            appendLine("Battery: $batteryScore/100")
            appendLine("Camera: $cameraScore/100")
            appendLine("Sensors: $sensorScore/100")
        }

        binding.txtRawInfo.text = buildString {
            appendLine("Model: Example Device")
            appendLine("Android Version: 13")
            appendLine("RAM: 8 GB")
            appendLine("Storage: 128 GB")
            appendLine("Display: 1080 x 2400, 120Hz")
            appendLine("Processor: Snapdragon 778G")
            appendLine("GPU: Adreno 642L")
            appendLine("Battery: 4300 mAh")
            appendLine("Camera: 64 MP main")
            appendLine("Sensors Status: Most major sensors available")
        }

        binding.btnPreviewReport.setOnClickListener {
            startActivity(Intent(this, ReportPreviewActivity::class.java))
        }

        binding.btnDownloadTxt.setOnClickListener {
            Toast.makeText(this, "TXT download will be added in next step", Toast.LENGTH_SHORT).show()
        }

        binding.btnDownloadPdf.setOnClickListener {
            Toast.makeText(this, "PDF download will be added in next step", Toast.LENGTH_SHORT).show()
        }

        binding.btnBackHomeFromResult.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun getVerdict(score: Int): String {
        return when {
            score >= 85 -> "Excellent phone for gaming, camera, and heavy daily use."
            score >= 70 -> "Very good phone for daily use, multitasking, and moderate gaming."
            score >= 55 -> "Average phone for normal daily tasks and light gaming."
            else -> "Basic phone, better for simple use only."
        }
    }
}
