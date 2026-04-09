package com.buymyphone.app.ui.heavyapps

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.buymyphone.app.databinding.ActivityHeavyAppsDetectorBinding
import java.io.File
import java.util.Locale

class HeavyAppsDetectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHeavyAppsDetectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeavyAppsDetectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadApps()

        binding.btnRefreshHeavyApps.setOnClickListener {
            loadApps()
        }

        binding.btnBackHeavyApps.setOnClickListener {
            finish()
        }
    }

    private fun loadApps() {
        val pm = packageManager
        val packages = pm.getInstalledPackages(0)

        val appItems = packages.mapNotNull { pkg ->
            try {
                val appInfo = pkg.applicationInfo
                val appName = pm.getApplicationLabel(appInfo).toString()
                val sourceFile = appInfo.sourceDir ?: return@mapNotNull null
                val fileSize = File(sourceFile).length()
                "$appName — ${formatSize(fileSize)}"
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending {
            extractSizeNumber(it)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            appItems
        )

        binding.listHeavyApps.adapter = adapter
    }

    private fun extractSizeNumber(text: String): Double {
        val regex = Regex("""([\d.]+)\s*(B|KB|MB|GB)""")
        val match = regex.find(text) ?: return 0.0
        val value = match.groupValues[1].toDoubleOrNull() ?: return 0.0
        val unit = match.groupValues[2]

        return when (unit) {
            "GB" -> value * 1024 * 1024 * 1024
            "MB" -> value * 1024 * 1024
            "KB" -> value * 1024
            else -> value
        }
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes >= 1024L * 1024L * 1024L ->
                String.format(Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))

            bytes >= 1024L * 1024L ->
                String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))

            bytes >= 1024L ->
                String.format(Locale.US, "%.2f KB", bytes / 1024.0)

            else -> "$bytes B"
        }
    }
}
