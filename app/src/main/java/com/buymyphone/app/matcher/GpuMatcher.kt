package com.buymyphone.app.matcher

import android.content.Context
import com.buymyphone.app.model.GpuScoreModel
import com.buymyphone.app.repository.AssetLoader

object GpuMatcher {

    fun findBestGpuMatch(context: Context, detectedGpuRenderer: String): GpuScoreModel? {
        if (detectedGpuRenderer.isBlank() || detectedGpuRenderer.equals("Unknown", ignoreCase = true)) {
            return null
        }

        val gpuList = AssetLoader.loadGpuScores(context)
        val normalizedDetected = normalize(detectedGpuRenderer)

        return gpuList.firstOrNull { item ->
            normalizedDetected.contains(normalize(item.name)) ||
                    normalize(item.name).contains(normalizedDetected)
        }
    }

    private fun normalize(text: String): String {
        return text
            .lowercase()
            .replace("-", " ")
            .replace("_", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
